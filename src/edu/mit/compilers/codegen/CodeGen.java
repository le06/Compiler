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
    
    
    Writer out;
    HashMap<String, String> currentLocs;
    HashMap<String, String> regMap;
    HashMap<String, Integer> methodSizeMap;
    
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
    }
    
    private void println(String s) {
        try {
            out.write(s + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write");
        }
    }
    
    private void writeHead() {
        println(".globl main");
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
        println(".space 8");
    }

    @Override
    public void visit(LLArrayDecl node) {
        println(".data");
        println("." + node.getLabel() + ":");
        println("\t.space (8 * " + String.valueOf(node.getMalloc().getSize())
                                + ")");
    }

    @Override
    public void visit(LLMalloc node) {
        // Do nothing
    }

    @Override
    public void visit(LLMethodDecl node) {
        println(node.getName() + ":");
        println("\tenter $(8 * " + 
        		methodSizeMap.get(node.getName()) + 
        		"), $0");
        
        // TODO - get args
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
            	if (currentLocs.containsKey(src.getLabel())) {
            		source = currentLocs.get(src.getLabel());
            	} else {
            		source = src.addressOfResult();
            	}
            	
            	println("\tmov " + source + ", " + R10);
            	println("\tmov " + R10 + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLIntLiteral) {
            	source = "$" + ((LLIntLiteral)node.getExpr()).getValue();
            	
            	println("\tmov " + source + ", " + R10);
            	println("\tmov " + R10 + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLBinaryOp) {
            	node.getExpr().accept(this);
            	source = R11;
            	
            	println("\tmov " + source + ", " + loc_addr);
            } else if (node.getExpr() instanceof LLMethodCall) {
            	writeMethodCall((LLMethodCall)node.getExpr());
            	source = RAX;
            	
            	println("\tmov " + source + ", " + loc_addr);
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
        
        for (int i = params.size()-1; i >= 0; i--) {
            pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
        }
        
        println("\tcall " + m.getMethodName());
        
        // Pop excess off the stack
        for (int i = 6; i < params.size(); i++) {
        	println("\tpop " + R10);
        }
    }
    
    private void writeCalloutCall(LLCallout m) {
    	// visit each of the params in this expression.
        ArrayList<LLExpression> params = m.getParams();
        /*for (LLExpression p : params) { Params should already be 
            p.accept(this);
        }*/
        
        for (int i = params.size()-1; i >= 0; i--) {
            pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
        }
        
        println("\tmov $0, " + RAX);
        println("\tcall " + m.getFnName().substring(1, m.getFnName().length()-1));
        
        // Pop excess off the stack
        for (int i = 6; i < params.size(); i++) {
        	println("\tpop " + R10);
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLArrayLocation node) {
        // TODO Auto-generated method stub
        
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
        } else {
        	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
        }
        
        if (r instanceof LLVarLocation) {
        	
        	rhs = ((LLVarLocation)r).addressOfResult();
        } else if (r instanceof LLIntLiteral) {
        	rhs = "$" + ((LLIntLiteral)r).getValue();
        } else {
        	throw new RuntimeException("Not yet implemented in codegen (BinOp)");
        }
        
        switch (node.getOp()) {
        case PLUS:
        	println("\tmov " + lhs + ", " + R10);
            println("\tmov " + rhs + ", " + R11);
        	println("\tadd " + R10 + ", " + R11);
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
        	println("\tmov " + lhs + ", " + R10);
            println("\tmov " + rhs + ", " + R11);
        	println("\tidiv " + R10 + ", " + R11);
        	break;
        case MOD:
        	println("\tmov " + lhs + ", " + R10);
            println("\tmov " + rhs + ", " + R11);
        	println("\tidiv " + R10 + ", " + R11);
        	break;
        default:
        	throw new RuntimeException("BinOp not yet implemented in codegen");	
        }
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLUnaryNot node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(LLIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLJump node) {
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLReturn node) {
        if (node.hasReturn()) {
            if (node.getExpr() instanceof LLVarLocation) {
                LLVarLocation loc = (LLVarLocation)node.getExpr();
                
                // If it is currently somewhere other than RAX
                if (currentLocs.containsKey(loc.getLabel()) &&
                   !currentLocs.get(loc.getLabel()).equals(RAX)) {
                    
                    println("\tmov " + currentLocs.get(loc.getLabel()) +
                                           ", " + RAX);
                // If we don't know where it is get it from the stack
                } else if (!currentLocs.containsKey(loc.getLabel())) {
                    println("\tmov " + loc.addressOfResult() + ", " + RAX);
                } else {
                    // Do nothing, it is already in RAX
                }
            }
        } else { // Return 0
            println("\tmov $0, " + RAX);
        }
        
        println("\tleave");
        println("\tret");
    }

    @Override
    public void visit(LLNop node) {
        // Do nothing
    }

}
