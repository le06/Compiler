package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.codegen.ll.*;

public class CodeGen implements LLNodeVisitor {
    /*
     * Constant values.
     */
    private final String RAX = "%rax";
    private final String RBP = "(%rbp)";    
    private final String RDI = "%rdi";
    private final String RSI = "%rsi";
    private final String RDX = "%rdx";
    private final String RCX = "%rcx";
    private final String R8  = "%r8";
    private final String R9  = "%r9";
    private final String R10 = "%r10";
    private final String R11 = "%r11";
    private final String R12 = "%r12";
    private final String R13 = "%r13";
    private final String R14 = "%r14";
    private final String R15 = "%r15";
    
    
    Writer out;
    HashMap<String, String> currentLocs;
    HashMap<String, String> regMap;
    HashMap<String, Integer> methodSizeMap;
    
    private LLMethodDecl currentMethod = null;
    
    public void gen(ArrayList<LLNode> instrs, HashMap<String, Integer> methodMap, Writer output) {
        out = output;
        currentLocs = new HashMap<String, String>();
        initRegMap();
        
        methodSizeMap = methodMap;
        
        // Write file header
        writeHead();
        
        // Write output for each instruction
        for (LLNode instr : instrs) {
            instr.accept(this);
        }
        
		println(".ARRAY_OUT_OF_BOUNDS:");
		println("\tmov $.error, %rdi");
		println("\tmov $0, %rax");
		println("\tcall printf");
		println("\tmov $1, %rax");
		println("\tint $0x80");
		println(".MISSING_RETURN:");
		println("\tmov $.error_1, %rdi");
		println("\tmov $0, %rax");
		println("\tcall printf");
		println("\tmov $1, %rax");
		println("\tint $0x80");
		println(".DIVIDE_BY_ZERO:");
		println("\tmov $.error_2, %rdi");
		println("\tmov $0, %rax");
		println("\tcall printf");
		println("\tmov $1, %rax");
		println("\tint $0x80");

    }
    
    private void println(String s) {
        try {
            out.write(s + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write");
        }
    }
    
    private void writeHead() {
        println(".data");
    }
    
    private void initRegMap() {
        regMap = new HashMap<String, String>();
        regMap.put(RAX, null);
        regMap.put(RBP, null);
        regMap.put(RDI, null);
        regMap.put(RSI, null);
        regMap.put(RDX, null);
        regMap.put(RCX, null);
        regMap.put(R8, null);
        regMap.put(R9, null);
        regMap.put(R10, null);
        regMap.put(R11, null);
    }

    @Override
    public void visit(LLFile node) {
        throw new RuntimeException("Unexpected LLFile");
    }

    @Override
    public void visit(LLGlobalDecl node) {
        println("." + node.getLabel() + ":");
        println("\t.space 8");
    }

    @Override
    public void visit(LLArrayDecl node) {
        println("\t.data");
        println("." + node.getLabel() + ":");
        println("\t.space (8 * " + String.valueOf(node.getMalloc().getSize())
                                + ")");
    }

    @Override
    public void visit(LLMalloc node) {
        // Do nothing
    }

    boolean first_method = true;
    
    @Override
    public void visit(LLMethodDecl node) {
    	currentMethod = node;
    	
    	if (first_method) {
    		println("\t.globl main");
    		first_method = false;
    	}
    	
        println(node.getName() + ":");
        println("\tenter $(8 * " + 
        		methodSizeMap.get(node.getName()) + 
        		"), $0");
        
        // Callee save registers
        if (node.usesRegister(R12)) {
        	println("\tpush " + R12);
        }
        if (node.usesRegister(R13)) {
        	println("\tpush " + R13);
        }
        if (node.usesRegister(R14)) {
        	println("\tpush " + R14);
        }
        if (node.usesRegister(R15)) {
        	println("\tpush " + R15);
        }
        
        ArrayList<LLVarLocation> args = node.getArgs();
		for (int i = 0; i < args.size(); i++) {
			loadArgument(i, args.get(i));
		}
    }
    
    private void loadArgument(int n, LLVarLocation var) {
    	String addr = var.addressOfResult();
    	LLMov mov;
    	// regs in order: rdi, rsi, rdx, rcx, r8, r9.
    	switch (n) {
    	case 0:
    		mov = new LLMov(RDI, addr);
    		mov.accept(this);
    		break;
    	case 1:
    		mov = new LLMov(RSI, addr);
    		mov.accept(this);
    		break;
    	case 2:
    		mov = new LLMov(RDX, addr);
    		mov.accept(this);
    		break;
    	case 3:
    		mov = new LLMov(RCX, addr);
    		mov.accept(this);
    		break;
    	case 4:
    		mov = new LLMov(R8, addr);
    		mov.accept(this);
    		break;
    	case 5:
    		mov = new LLMov(R9, addr);
    		mov.accept(this);
    		break;
    	default: // 16+8*0,1,2,3...
    		long offset = 16+8*(n-6);
    		String arg_addr = String.valueOf(offset) + RBP;
    		
    		// memory > address > memory.    		
    		mov = new LLMov(arg_addr, R10);
    		LLMov mov2 = new LLMov(R10, addr);
    		mov.accept(this);
    		mov2.accept(this);
    		break;
    	}
    }

    @Override
    public void visit(LLEnvironment node) {
        throw new RuntimeException("Unexpected LLEnvironment");
    }
    
    private void emitBinOp(LLVarLocation loc, LLBinaryOp node) {
        LLExpression l = node.getLhs();
        LLExpression r = node.getRhs();
        
        boolean isPlusEqual = false;
        String lhs, rhs;
        
        // Different forms: a += 3, a = b + 3, a = b + c, a += c
        
        boolean opAssociative = false;
        switch (node.getOp()) {
        case PLUS:
        case MUL:
        case EQ:
        case NEQ:
        case AND:
        case OR:
        	opAssociative = true;
        	break;
        default:
        	opAssociative = false;
        	break;
        }
        
        
        // If of the form a = a + ?
        if (opAssociative &&
        	   l instanceof LLVarLocation && 
        	   ((LLVarLocation)l).addressOfResult().equals(loc.addressOfResult())) {
        	isPlusEqual = true;
        	lhs = loc.addressOfResult();
        	
        	if (r instanceof LLVarLocation) {
            	rhs = ((LLVarLocation)r).addressOfResult();
            } else if (r instanceof LLIntLiteral) {
            	rhs = "$" + ((LLIntLiteral)r).getValue();
            } else if (r instanceof LLBoolLiteral) {
            	if (((LLBoolLiteral)r).getValue()) {
            		rhs = "$1";
            	} else {
            		rhs = "$0";
            	}
            } else {
            	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
            }
        // If of the form a = ? + a
        } else if (opAssociative &&
        		r instanceof LLVarLocation && 
        		  ((LLVarLocation)r).addressOfResult().equals(loc.addressOfResult())) {
        	isPlusEqual = true;
        	lhs = loc.addressOfResult();
        	
        	if (l instanceof LLVarLocation) {
            	rhs = ((LLVarLocation)l).addressOfResult();
            } else if (l instanceof LLIntLiteral) {
            	rhs = "$" + ((LLIntLiteral)l).getValue();
            } else if (l instanceof LLBoolLiteral) {
            	if (((LLBoolLiteral)l).getValue()) {
            		rhs = "$1";
            	} else {
            		rhs = "$0";
            	}
            } else {
            	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
            }
        } else {
        	if (l instanceof LLVarLocation) {
            	lhs = ((LLVarLocation)l).addressOfResult();
            } else if (l instanceof LLIntLiteral) {
            	lhs = "$" + ((LLIntLiteral)l).getValue();
            } else if (l instanceof LLBoolLiteral) {
            	if (((LLBoolLiteral)l).getValue()) {
            		lhs = "$1";
            	} else {
            		lhs = "$0";
            	}
            } else {
            	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
            }
        	
        	if (r instanceof LLVarLocation) {
            	rhs = ((LLVarLocation)r).addressOfResult();
            } else if (r instanceof LLIntLiteral) {
            	rhs = "$" + ((LLIntLiteral)r).getValue();
            } else if (r instanceof LLBoolLiteral) {
            	if (((LLBoolLiteral)r).getValue()) {
            		rhs = "$1";
            	} else {
            		rhs = "$0";
            	}
            } else {
            	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
            }
        }
        
        
        switch (node.getOp()) {
        case PLUS:
        	if (loc.inRegister() && isPlusEqual) {
        		println("\taddq " + rhs + ", " + lhs);
        	} else if (isPlusEqual) {
        		println("\tmovq " + rhs + ", " + RAX);
        		println("\taddq " + RAX + ", " + lhs);
        	} else {
	        	//println("\tmovq " + lhs + ", " + R11);
	            println("\tmovq " + rhs + ", " + RAX);
	        	println("\taddq " + lhs + ", " + RAX);
	        	println("\tmovq " + RAX + ", " + loc.addressOfResult());
        	}
        	break;
        case MINUS:
        	if (loc.inRegister() && isPlusEqual) {
        		println("\tsubq " + rhs + ", " + lhs);
        	} else if (isPlusEqual) {
        		println("\tmovq " + rhs + ", " + RAX);
        		println("\tsubq " + RAX + ", " + lhs);
        	} else {
	        	//println("\tmovq " + lhs + ", " + R11);
	            println("\tmovq " + lhs + ", " + RAX);
	        	println("\tsubq " + rhs + ", " + RAX);
	        	println("\tmovq " + RAX + ", " + loc.addressOfResult());
        	}
        	break;
        case MUL:
        	if (loc.inRegister() && isPlusEqual) {
        		println("\timul " + rhs + ", " + lhs);
        	} else if (isPlusEqual) {
        		println("\tmovq " + rhs + ", " + RAX);
        		println("\timul " + RAX + ", " + lhs);
        	} else {
	        	//println("\tmovq " + lhs + ", " + R11);
	            println("\tmovq " + rhs + ", " + RAX);
	        	println("\timul " + lhs + ", " + RAX);
	        	println("\tmovq " + RAX + ", " + loc.addressOfResult());
        	}
        	break;
        case DIV:
        	if (r instanceof LLIntLiteral) {
        		if (((LLIntLiteral)r).getValue() == 0) {
        			println("jmp .DIVIDE_BY_ZERO");
        			break;
        		}
        	} else {
	        	println("\tcmp $0, " + rhs);
	        	println("je .DIVIDE_BY_ZERO");
        	}
        	println("\tmovq " + lhs + ", " + RAX);
            println("\tmovq " + RAX + ", " + RDX);
            println("\tsarq " + "$63" + ", " + RDX);
            println("\tmovq " + rhs + ", " + R11);
        	println("\tidivq " + R11);
        	println("\tmovq " + RAX + ", " + loc.addressOfResult());
        	break;
        case MOD:
        	if (r instanceof LLIntLiteral) {
        		if (((LLIntLiteral)r).getValue() == 0) {
        			println("jmp .DIVIDE_BY_ZERO");
        			break;
        		}
        	} else {
	        	println("\tcmp $0, " + rhs);
	        	println("je .DIVIDE_BY_ZERO");
        	}
        	println("\tmovq " + lhs + ", " + RAX);
            println("\tmovq " + RAX + ", " + RDX);
            println("\tsarq " + "$63" + ", " + RDX);
            println("\tmovq " + rhs + ", " + R11);
        	println("\tidivq " + R11);
        	println("\tmovq " + RDX + ", " + loc.addressOfResult());
        	break;
        case EQ:
        	println("\tmovq " + rhs + ", " + R11);
            println("\tmovq " + lhs + ", " + RAX);
        	println("\tcmp " + R11 + ", " + RAX);
        	println("\tmovq $1, " + R11);
        	println("\tmovq $0, " + RAX);
        	println("\tcmove " + R11 + ", " + RAX);
        	println("\tmovq " + RAX + ", " + loc.addressOfResult());
        	break;
        	//TODO
        default:
        	throw new RuntimeException("BinOp not yet implemented in codegen");	
        }
    }

    @Override
    public void visit(LLAssign node) {
    	String source, loc_addr;
    	
        if (node.getLoc() instanceof LLVarLocation) {
        	LLVarLocation loc = (LLVarLocation)node.getLoc();
        	if (currentLocs.containsKey(loc.getLabel())) {
        		loc_addr = currentLocs.get(loc.getLabel());
        	} else {
        		loc_addr = loc.addressOfResult();
        	}
        	
        	if (node.getExpr() instanceof LLVarLocation) {
            	LLVarLocation src = (LLVarLocation)node.getExpr();
            	if (src.inRegister() || loc.inRegister()) {
            		println("\tmovq " + src.addressOfResult() + ", " + loc.addressOfResult());
            	} else {
                	println("\tmovq " + src.addressOfResult() + ", " + RAX);
                	println("\tmovq " + RAX + ", " + loc_addr);
            	}
        	} else if (node.getExpr() instanceof LLIntLiteral) {
            	source = "$" + ((LLIntLiteral)node.getExpr()).getValue();
            	
            	println("\tmovq " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLMethodCall) {
            	writeMethodCall((LLMethodCall)node.getExpr());
            	source = RAX;
            	
            	println("\tmov " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLUnaryNeg) {
            	LLVarLocation rhs = (LLVarLocation)((LLUnaryNeg)node.getExpr()).getExpr();
            	// If this is of the form reg = -reg, just negate the register
            	if (rhs.inRegister() && rhs.addressOfResult() == loc.addressOfResult()) {
            		println("\tneg " + rhs.addressOfResult());
            	} else {
		            println("\tmovq " + rhs.addressOfResult() + ", " + RAX);
		            println("\tneg " + RAX);
		            println("\tmovq " + RAX + ", " + loc_addr);
            	}
            } else if (node.getExpr() instanceof LLArrayLocation) {
            	LLArrayLocation dest = (LLArrayLocation)node.getExpr();
            	source = prepareArrayLocation(dest);
            	if (loc.inRegister()) {
            		println("\tmovq " + source + ", " + loc.addressOfResult());
            	} else {
	            	println("\tmovq " + source + ", " + RAX);
	            	println("\tmovq " + RAX + ", " + loc_addr);
            	}
            } else if (node.getExpr() instanceof LLBoolLiteral) { 
            	if (((LLBoolLiteral)node.getExpr()).getValue()) {
            		println("\tmovq $1, " + loc_addr);
            	} else {
            		println("\tmovq $0, " + loc_addr);
            	}
            } else if (node.getExpr() instanceof LLUnaryNot) {
            	LLUnaryNot n = (LLUnaryNot)node.getExpr();
        		LLVarLocation src = (LLVarLocation)n.getExpr();
        		if (src.inRegister()) {
        			println("\tcmp $0, " + src.addressOfResult());
        		} else {
        			println("\tmovq " + src.addressOfResult() + ", " + RAX);
            		println("\tcmp $0, " + RAX);
        		}
        		println("\tmov $1, " + R11);
        		if (loc.inRegister()) {
        			println("\tmov $0, " + loc.addressOfResult());
        			println("\tcmove " + R11 + ", " + loc.addressOfResult());
        		} else {
	            	println("\tmov $0, " + RAX);
	            	println("\tcmove " + R11 + ", " + RAX);
	        		println("\tmovq " + RAX + ", " + loc_addr);
        		}
            } else if (node.getExpr() instanceof LLMethodCall) {
            	writeMethodCall((LLMethodCall)node.getExpr());
            	println("\tmovq " + RAX + ", " + loc.addressOfResult());
            } else if (node.getExpr() instanceof LLCallout) {
            	LLCallout c = (LLCallout)node.getExpr();
            	writeCalloutCall(c);
            	println("\tmovq " + RAX + ", " + loc.addressOfResult());
            } else if (node.getExpr() instanceof LLBinaryOp) {
            	emitBinOp(loc, (LLBinaryOp)node.getExpr());
            } else {
            	throw new RuntimeException("Unimplemented in CodeGen, LLAssign");
            }
        	
        // Deal with Arrays separately
        } else if (node.getLoc() instanceof LLArrayLocation) {
        	LLArrayLocation loc = (LLArrayLocation)node.getLoc();
        	loc_addr = prepareArrayLocation(loc);
        	
        	if (node.getExpr() instanceof LLVarLocation) {
            	LLVarLocation src = (LLVarLocation)node.getExpr();
            	if (currentLocs.containsKey(src.getLabel())) {
            		source = currentLocs.get(src.getLabel());
            	} else {
            		source = src.addressOfResult();
            	}
            	
            	println("\tmovq " + src.addressOfResult() + ", " + R10);
            	println("\tmovq " + R10 + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLIntLiteral) {
            	source = "$" + ((LLIntLiteral)node.getExpr()).getValue();
            	
            	println("\tmovq " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLBinaryOp) {
            	node.getExpr().accept(this);
            	source = R11;
            	println("\tmovq " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLMethodCall) {
            	writeMethodCall((LLMethodCall)node.getExpr());
            	source = RAX;
            	
            	println("\tmovq " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLUnaryNeg) {
            	LLVarLocation rhs = (LLVarLocation)((LLUnaryNeg)node.getExpr()).getExpr();
            	println("\tmovq " + rhs.addressOfResult() + ", " + R10);
            	println("\tneg " + R10);
            	println("\tmovq " + R10 + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLArrayLocation) {
            	LLArrayLocation dest = (LLArrayLocation)node.getExpr();
            	source = prepareArrayLocation(dest);
            	println("\tmovq " + source + ", " + R11);
            	println("\tmovq " + R11 + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLCallout) {
            	LLCallout c = (LLCallout)node.getExpr();
            	writeCalloutCall(c);
            	println("\tmovq " + RAX + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLBoolLiteral) { 
            	if (((LLBoolLiteral)node.getExpr()).getValue()) {
            		println("\tmovq $1, " + loc_addr);
            	} else {
            		println("\tmovq $0, " + loc_addr);
            	}
            } else if (node.getExpr() instanceof LLUnaryNot) {
            	LLUnaryNot n = (LLUnaryNot)node.getExpr();
            	if (n.getExpr() instanceof LLVarLocation) {
            		LLVarLocation src = (LLVarLocation)n.getExpr();
            		println("\tmovq " + src.addressOfResult() + ", " + R11);
            		println("\tcmp $0, " + R11);
            		println("\tmovq $1, " + R10);
                	println("\tmovq $0, " + R11);
                	println("\tcmove " + R10 + ", " + R11);
            		println("\tmovq " + R11 + ", " + loc_addr);
            	} else {
                	throw new RuntimeException("Unimplemented in CodeGen, LLAssign");
                }
            } else {
            	throw new RuntimeException("Unimplemented in CodeGen, LLAssign");
            }
        } else {
        	throw new RuntimeException("Unimplemented in CodeGen, LLAssign");
        }
    }

    @Override
    public void visit(LLMethodCall node) {
        writeMethodCall(node);
    }
    
    private void writeMethodCall(LLMethodCall m) {
        // visit each of the params in this expression.
        ArrayList<LLExpression> params = m.getParams();
        /*for (LLExpression p : params) { Params should already be 
            p.accept(this);
        }*/
        
        // Store caller save registers
        if (currentMethod.usesRegister(RDI)) {
        	println("\tpush " + RDI);
        }
        if (currentMethod.usesRegister(RSI)) {
        	println("\tpush " + RSI);
        }
        if (currentMethod.usesRegister(RDX)) {
        	println("\tpush " + RDX);
        }
        if (currentMethod.usesRegister(RCX)) {
        	println("\tpush " + RCX);
        }
        if (currentMethod.usesRegister(R8)) {
        	println("\tpush " + R8);
        }
        if (currentMethod.usesRegister(R9)) {
        	println("\tpush " + R9);
        }
        
        for (int i = params.size()-1; i >= 0; i--) {
            pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
        }
        
        println("\tcall " + m.getMethodName());
        
        // Pop excess off the stack
        for (int i = 6; i < params.size(); i++) {
        	println("\tpop " + R10);
        }
        
        // Retrieve caller save registers
        if (currentMethod.usesRegister(R9)) {
        	println("\tpop " + R9);
        }
        if (currentMethod.usesRegister(R8)) {
        	println("\tpop " + R8);
        }
        if (currentMethod.usesRegister(RCX)) {
        	println("\tpop " + RCX);
        }
        if (currentMethod.usesRegister(RDX)) {
        	println("\tpop " + RDX);
        }
        if (currentMethod.usesRegister(RSI)) {
        	println("\tpop " + RSI);
        }
        if (currentMethod.usesRegister(RDI)) {
        	println("\tpop " + RDI);
        }
    }
    
    private void writeCalloutCall(LLCallout m) {
    	// visit each of the params in this expression.
        ArrayList<LLExpression> params = m.getParams();
        
        // Store caller save registers
        if (currentMethod.usesRegister(RDI)) {
        	println("\tpush " + RDI);
        }
        if (currentMethod.usesRegister(RSI)) {
        	println("\tpush " + RSI);
        }
        if (currentMethod.usesRegister(RDX)) {
        	println("\tpush " + RDX);
        }
        if (currentMethod.usesRegister(RCX)) {
        	println("\tpush " + RCX);
        }
        if (currentMethod.usesRegister(R8)) {
        	println("\tpush " + R8);
        }
        if (currentMethod.usesRegister(R9)) {
        	println("\tpush " + R9);
        }
        
        for (int i = params.size()-1; i >= 0; i--) {
            pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
        }
        
        println("\tmov $0, " + RAX);
        println("\tcall " + m.getFnName().substring(1, m.getFnName().length()-1));
        
        // Pop excess off the stack
        for (int i = 6; i < params.size(); i++) {
        	println("\tpop " + R10);
        }
        
        // Retrieve caller save registers
        if (currentMethod.usesRegister(R9)) {
        	println("\tpop " + R9);
        }
        if (currentMethod.usesRegister(R8)) {
        	println("\tpop " + R8);
        }
        if (currentMethod.usesRegister(RCX)) {
        	println("\tpop " + RCX);
        }
        if (currentMethod.usesRegister(RDX)) {
        	println("\tpop " + RDX);
        }
        if (currentMethod.usesRegister(RSI)) {
        	println("\tpop " + RSI);
        }
        if (currentMethod.usesRegister(RDI)) {
        	println("\tpop " + RDI);
        }
    }
    
    private void pushArgument(int n, LLExpression arg) {
        // polymorphic, so we don't have to check if arg is
        // a StringLiteral or not.
        String addr = arg.addressOfResult();
        
        LLMov mov;
        // regs in order: rdi, rsi, rdx, rcx, r8, r9.
        switch (n) {
        case 0:
            mov = new LLMov(addr, RDI);
            mov.accept(this);
            break;
        case 1:
            mov = new LLMov(addr, RSI);
            mov.accept(this);
            break;
        case 2:
            mov = new LLMov(addr, RDX);
            mov.accept(this);
            break;
        case 3:
            mov = new LLMov(addr, RCX);
            mov.accept(this);
            break;
        case 4:
            mov = new LLMov(addr, R8);
            mov.accept(this);
            break;
        case 5:
            mov = new LLMov(addr, R9);
            mov.accept(this);
            break;
        default:
            mov = new LLMov(addr, R10);
            mov.accept(this);
            println("\tpush " + R10);
            break;
        }
    }

    @Override
    public void visit(LLCallout node) {
        writeCalloutCall(node);
    }

    @Override
    public void visit(LLStringLiteral node) {
        println(node.getLabelASM());
        println("\t.string " +
        		node.getText());
    }

    @Override
    public void visit(LLVarLocation node) {
        // Shouldn't occur
    }

    @Override
    public void visit(LLArrayLocation node) {
        // Shouldn't occur
    }
    
    private String prepareArrayLocation(LLArrayLocation a) {
    	if (a.getIndexExpr() instanceof LLIntLiteral) {
    		long val = ((LLIntLiteral)a.getIndexExpr()).getValue();
    		if (val < 0 || val >= a.getSize()) {
    			println("\tjmp .ARRAY_OUT_OF_BOUNDS");
    		}
    		println("\tmov $" + val + ", " + RAX);
    	} else if (a.getIndexExpr() instanceof LLVarLocation) {
    		LLVarLocation src = (LLVarLocation)a.getIndexExpr();
    		println("\tmovq " + src.addressOfResult() + ", " + RAX);
    		println("\tmovq $0, " + R11);
    		println("\tcmp " + R11 + ", " + RAX); // If index less than 0, jump AOOB
    		println("\tjl  .ARRAY_OUT_OF_BOUNDS");
    		println("\tmovq $" + a.getSize() + ", " + R11);
    		println("\tcmp " + R11 + ", " + RAX);
    		println("\tjge  .ARRAY_OUT_OF_BOUNDS"); // If index >= size of array, jump AOOB
    	} else {
    		throw new RuntimeException("Unexpected array index expression");
    	}
    	println("\timulq $8, " + RAX);
    	return "." + a.getLocation() + "(" + RAX + ")";
    }


    @Override
    public void visit(LLBinaryOp node) {
        LLExpression l = node.getLhs();
        LLExpression r = node.getRhs();
        
        String lhs, rhs;
        
        if (l instanceof LLVarLocation) {
        	lhs = ((LLVarLocation)l).addressOfResult();
        } else if (l instanceof LLIntLiteral) {
        	lhs = "$" + ((LLIntLiteral)l).getValue();
        } else if (l instanceof LLBoolLiteral) {
        	if (((LLBoolLiteral)l).getValue()) {
        		lhs = "$1";
        	} else {
        		lhs = "$0";
        	}
        } else {
        	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
        }
        
        if (r instanceof LLVarLocation) {
        	
        	rhs = ((LLVarLocation)r).addressOfResult();
        } else if (r instanceof LLIntLiteral) {
        	rhs = "$" + ((LLIntLiteral)r).getValue();
        } else if (r instanceof LLBoolLiteral) {
        	if (((LLBoolLiteral)r).getValue()) {
        		rhs = "$1";
        	} else {
        		rhs = "$0";
        	}
        } else {
        	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
        }
        
        switch (node.getOp()) {
        case PLUS:
        	//println("\tmov " + lhs + ", " + R10);
            println("\tmovq " + rhs + ", " + RAX);
        	println("\taddq " + lhs + ", " + RAX);
        	break;
        case MINUS:
        	println("\tmov " + rhs + ", " + R10);
            println("\tmov " + lhs + ", " + R11);
        	println("\tsub " + R10 + ", " + R11);
        	break;
        case MUL:
        	println("\tmov " + lhs + ", " + R10);
            println("\tmov " + rhs + ", " + R11);
        	println("\timul " + R10 + ", " + R11);
        	break;
        case DIV:
        	if (r instanceof LLIntLiteral) {
        		if (((LLIntLiteral)r).getValue() == 0) {
        			println("jmp .DIVIDE_BY_ZERO");
        			break;
        		}
        	} else {
	        	println("\tcmp $0, " + rhs);
	        	println("je .DIVIDE_BY_ZERO");
        	}
        	println("\tmovq " + lhs + ", " + RAX);
            println("\tmovq " + RAX + ", " + RDX);
            println("\tsarq " + "$63" + ", " + RDX);
            println("\tmovq " + rhs + ", " + R11);
        	println("\tidivq " + R11);
        	println("\tmovq " + RAX + ", " + R11);
        	break;
        case MOD:
        	if (r instanceof LLIntLiteral) {
        		if (((LLIntLiteral)r).getValue() == 0) {
        			println("jmp .DIVIDE_BY_ZERO");
        			break;
        		}
        	} else {
	        	println("\tcmp $0, " + rhs);
	        	println("je .DIVIDE_BY_ZERO");
        	}
        	println("\tmovq " + lhs + ", " + RAX);
            println("\tmovq " + RAX + ", " + RDX);
            println("\tsarq " + "$63" + ", " + RDX);
            println("\tmovq " + rhs + ", " + R11);
        	println("\tidivq " + R11);
        	println("\tmovq " + RDX + ", " + R11);
        	break;
        case EQ:
        	println("\tmov " + rhs + ", " + R10);
            println("\tmov " + lhs + ", " + R11);
        	println("\tcmp " + R10 + ", " + R11);
        	println("\tmov $1, " + R10);
        	println("\tmov $0, " + R11);
        	println("\tcmove " + R10 + ", " + R11);
        	break;
        	//TODO
        default:
        	throw new RuntimeException("BinOp not yet implemented in codegen");	
        }
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // Should not be called
    }

    @Override
    public void visit(LLUnaryNot node) {
        // Should not be called
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // Should not be called
    }

    @Override
    public void visit(LLIntLiteral node) {
        // Should not be called
    }

    @Override
    public void visit(LLJump node) {
        println("\t" + node.getOpcode() + " ." + node.getLabel());
    }

    @Override
    public void visit(LLLabel node) {
        println(node.getASMLabel());
    }

    @Override
    public void visit(LLMov node) {
        println("\tmov " + node.getSrc() + ", " + node.getDest());
    }

    @Override
    public void visit(LLCmp node) {
    	String left, right;
    	if (node.getL() instanceof LLVarLocation) {
    		LLVarLocation l = (LLVarLocation)node.getL();
    		if (l.inRegister()) {
    			left = l.addressOfResult();
    		} else {
    			left = RAX;
    			println("\tmov " + l.addressOfResult() + ", " + RAX);
    		}
    	} else {
    		left = RAX;
    		println("\tmov " + node.getL().addressOfResult() + ", " + RAX);
    	}
    	
    	if (node.getR() instanceof LLVarLocation) {
    		LLVarLocation r = (LLVarLocation)node.getR();
    		if (r.inRegister()) {
    			right = r.addressOfResult();
    		} else {
    			right = R11;
    			println("\tmov " + r.addressOfResult() + ", " + R11);
    		}
    	} else {
    		right = R11;
    		println("\tmov " + node.getR().addressOfResult() + ", " + R11);
    	}
/*    	println("\tmovq " + node.getL().addressOfResult() + ", " + RAX);
    	println("\tmovq " + node.getR().addressOfResult() + ", " + R11);*/
        println("\tcmp " + left + ", " + right);
    }

    @Override
    public void visit(LLReturn node) {
        if (node.hasReturn()) {
            if (node.getExpr() instanceof LLVarLocation) {
                LLVarLocation loc = (LLVarLocation)node.getExpr();
                println("\tmov " + loc.addressOfResult() + ", " + RAX);
                
/*                // If it is currently somewhere other than RAX
                if (currentLocs.containsKey(loc.getLabel()) &&
                   !currentLocs.get(loc.getLabel()).equals(RAX)) {
                    
                    println("\tmov " + currentLocs.get(loc.getLabel()) +
                                           ", " + RAX);
                // If we don't know where it is get it from the stack
                } else if (!currentLocs.containsKey(loc.getLabel())) {
                    println("\tmov " + loc.addressOfResult() + ", " + RAX);
                } else {
                    // Do nothing, it is already in RAX
                }*/
            } else if (node.getExpr() instanceof LLIntLiteral) {
            	println("\tmov $" + ((LLIntLiteral)node.getExpr()).getValue() + ", " + RAX);
            } else if (node.getExpr() instanceof LLBoolLiteral) {
            	LLBoolLiteral lit = (LLBoolLiteral)node.getExpr();
            	if (lit.getValue()) {
            		println("\tmov $1, " + RAX);
            	} else {
            		println("\tmov $0, " + RAX);
            	}
        	} else {
            	throw new RuntimeException("Unexpected return type in CodeGen");
            }
        } else { // Return 0
            println("\tmov $0, " + RAX);
        }
        
        // Callee save registers
        if (currentMethod.usesRegister(R12)) {
        	println("pop " + R12);
        }
        if (currentMethod.usesRegister(R13)) {
        	println("pop " + R13);
        }
        if (currentMethod.usesRegister(R14)) {
        	println("pop " + R14);
        }
        if (currentMethod.usesRegister(R15)) {
        	println("pop " + R15);
        }
        
        println("\tleave");
        println("\tret");
    }

    @Override
    public void visit(LLNop node) {
        // Do nothing
    }

}
