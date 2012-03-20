package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.LLLocation;
import edu.mit.compilers.codegen.ll.LLMalloc;
import edu.mit.compilers.codegen.ll.LLArrayLocation;
import edu.mit.compilers.codegen.ll.LLArrayDecl;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLBoolLiteral;
import edu.mit.compilers.codegen.ll.LLMov;
import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNodeVisitor;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLReturn;
import edu.mit.compilers.codegen.ll.LLStringLiteral;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLUnaryNot;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class CodeGenerator implements LLNodeVisitor {
    
	/*
	 * Constant values.
	 */
	private final String RAX = "%rax";
	
	private final String RDI = "%rdi";
	private final String RSI = "%rsi";
	private final String RDX = "%rdx";
	private final String RCX = "%rcx";
	private final String R8 = "%r8";
	private final String R9 = "%r9";
	private final String R10 = "%r10";
	private final String R11 = "%r11";
	
    /*
     * Printing functions.
     */
    
    private void writeText(String text) {
        try {
            outputStream.write(text);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(-1);
        }
    }
    
    private void writeLine(String text) {
    	for (int i = 0; i < tab_level; i++) {
    		writeText("\t");
    	}
    	writeText(text);
    	writeText("\n");
    }
    
    private String formatLine(String inst, String arg) {
    	String line;
    	if (inst.length() >= 4) {
    		line = inst + "\t" + arg;
    		return line;
    	} else {
    		line = inst + "\t\t" + arg;
    		return line;
    	}
    }
    
    private String formatLine(String inst, String arg1, String arg2) {
		String SEPARATOR = ", ";
    	
    	String line;
    	if (inst.length() >= 4) {
    		line = inst + "\t" + arg1 + SEPARATOR + arg2;
    		return line;
    	} else {
    		line = inst + "\t\t" + arg1 + SEPARATOR + arg2;
    		return line;
    	}
    }
    
    private Writer outputStream;
    private int tab_level;
    
    public CodeGenerator() {
    	tab_level = 0;
    }
    
    // entry point for LL tree walker.
    public void outputASM(Writer output, LLFile decafFile) {
        outputStream = output;
        decafFile.accept(this);
    }
    
    /*
     * Low-level node visitor methods.
     * These are responsible for generating the actual ASM.
     */
    
    @Override
    public void visit(LLFile node) {

        for (LLGlobalDecl g : node.getGlobalDecls()) {
        	g.accept(this);
        }
        
        for (LLArrayDecl a : node.getArrayDecls()) {
        	a.accept(this);
        }
        
        for (LLMethodDecl m : node.getMethods()) {
        	m.accept(this);
        }

        mainDirective(); // write ".globl main".
        node.getMain().accept(this);
        
        for (LLStringLiteral l : node.getStringLiterals()) {
        	l.accept(this);
        }
        
    	// TODO: array oob function
    	// TODO: missing return statement function
    }
    
    @Override
    public void visit(LLGlobalDecl node) {
        node.getLabel().accept(this);
        
        tab_level++;
        node.getMalloc().accept(this);
        tab_level--;
    }
    
    @Override
    public void visit(LLArrayDecl node) {
        node.getLabel().accept(this);
        
        tab_level++;
        node.getMalloc().accept(this);
        tab_level--;
    }    
    
	@Override
	public void visit(LLMalloc node) {
		long size = node.getSize();
		String inst = ".space"; // GCC directive.
		String arg;
		if (size > 1) {
			arg = "(" + "8 * " + String.valueOf(size) + ")";
		} else {
			arg = "8";
		}
		String text = formatLine(inst, arg);
		writeLine(text);
	}
    
///////////////////////////////////////////////////////////////////////////////
	
	private void mainDirective() {
		tab_level++;
		writeLine(".globl main");
		tab_level--;
	}
	
	@Override
	public void visit(LLMethodDecl node) {
		// generate function label.
		LLLabel label = new LLLabel(node.getName());
		label.accept(this);
		// node accept function goes to node environment next.
	}
	
    @Override
    public void visit(LLEnvironment node) {
		tab_level++;
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }
		tab_level--;
    }    
    
///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLAssign node) {
    	LLLocation loc = node.getLoc();
    	LLExpression expr = node.getExpr();
        
    	loc.accept(this);						// if array loc, need to check!
    	expr.accept(this);						// next, walk the expr.
    	String arg1 = expr.addressOfResult();	// temp memory location for evaluated expr.
    	String arg2 = loc.getLabel();
    	
    	if (loc instanceof LLArrayLocation) {
    		LLExpression index = ((LLArrayLocation)loc).getIndexExpr();
    		String index_val = index.addressOfResult();		// calculate index offset.
    		LLMov mov_index = new LLMov(index_val, R10);	// move offset to register.
    		mov_index.accept(this);							// write this in ASM.
    		
    		String mul_inst = "mul"; // change index into address offset.
    		String mul_val = "$8";	 // quadword size.
    		String mul_line = formatLine(mul_inst, mul_val, R10);
    		writeLine(mul_line);	 // write this in ASM.
    		
    		arg2 = arg2 + "(" + R10 + ")";	// i.e. array_label(%r10).
    	}
    	
		LLMov mov_to_reg = new LLMov(arg1, R11);	// move expr to register.
		mov_to_reg.accept(this);
    	
    	// move the result of the expr evaluation to loc!
    	LLMov mov_to_mem = new LLMov(R11, arg2);
    	mov_to_mem.accept(this);
    }    

    private void pushArgument(int n, LLExpression arg) {
    	String addr = arg.addressOfResult();
    	LLMov mov;
    	// regs in order: rdi, rsi, rdx, rcx, r8, r9.
    	switch (n) {
    	case 0:
    		mov = new LLMov(addr, RDI);
    		mov.accept(this);
    	case 1:
    		mov = new LLMov(addr, RSI);
    		mov.accept(this);
    	case 2:
    		mov = new LLMov(addr, RDX);
    		mov.accept(this);
    	case 3:
    		mov = new LLMov(addr, RCX);
    		mov.accept(this);
    	case 4:
    		mov = new LLMov(addr, R8);
    		mov.accept(this);
    	case 5:
    		mov = new LLMov(addr, R9);
    		mov.accept(this);
    	default:
    		mov = new LLMov(addr, R10);
    		mov.accept(this);
    		String inst = "push"; // push arg onto stack!
    		String src = R10;
    		String push_line = formatLine(inst, src);
    		writeLine(push_line);
    	}
    }
    
    @Override
    public void visit(LLMethodCall node) {
    	// arg exprs are evaluated in accept(). read those values into regs!
    	ArrayList<LLExpression> params = node.getParams();
    	for (int i = node.getNumParams(); i >= 0; i--) {
    		pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
    	}
    	
    	String call_inst = "call";
    	String call_arg = node.getMethodName();
    	String call_line = formatLine(call_inst, call_arg);
    	writeLine(call_line);
    	
    	String expr_addr = node.addressOfResult();
    	if (expr_addr != null) {
    		LLMov mov_result = new LLMov(RAX, expr_addr);
    		mov_result.accept(this);
    	}
    }
    
    @Override
    public void visit(LLCallout node) {

    }    
    
    @Override
    public void visit(LLStringLiteral node) {
        writeText(node.getLabelASM() + "\n\t.string " +
                  node.getText() + "\n");
    }

///////////////////////////////////////////////////////////////////////////////    
    
    @Override
    public void visit(LLVarLocation node) {
    	boolean is_expr = false;
    	if (node.addressOfResult() != null) {
    		is_expr = true;
    	}
    	
    	if (is_expr) {
    		String src = node.getLabel();
    		String dest = node.addressOfResult();
    		LLMov mov1 = new LLMov(src, R10);	// memory > address > memory.
    		mov1.accept(this);
    		LLMov mov2 = new LLMov(R10, dest);
    		mov2.accept(this);
    	}
    }
    
    @Override
    public void visit(LLArrayLocation node) {
    	// check bounds!
    	LLExpression index = node.getIndexExpr();
    	index.accept(this);
    	
    	// TODO: auto-gen bounds checking ASM.
    	
    	boolean is_expr = false;
    	if (node.addressOfResult() != null) {
    		is_expr = true;
    	}
    	
    	if (is_expr) {
    		String index_val = index.addressOfResult();		// calculate value of index.
    		LLMov mov_index = new LLMov(index_val, R10);	// move index to register.
    		mov_index.accept(this);							// write this in ASM.
    		
    		String mul_inst = "mul"; // change index into address offset.
    		String mul_val = "$8";	 // quadword size.
    		String mul_line = formatLine(mul_inst, mul_val, R10);
    		writeLine(mul_line);	 // write this in ASM.
    		
    		String src = node.getLabel();
    		src += "(" + R10 + ")"; // i.e. array_label(%r10).
    		
    		// move the array-offset contents to the dest address.
    		String dest = node.addressOfResult();
    		LLMov mov1 = new LLMov(src, R11);	// memory > address > memory.
    		mov1.accept(this);
    		LLMov mov2 = new LLMov(R11, dest);
    		mov2.accept(this);
    	}
    	
    }

///////////////////////////////////////////////////////////////////////////////

    @Override
    public void visit(LLBinaryOp node) {
        // TODO Auto-generated method stub
        
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

///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLJump node) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(LLLabel node) {
    	String name = node.getName();
    	writeLine(name + ":");
    }

	@Override
	public void visit(LLMov node) {
		String inst = "mov";
		String src = node.getSrc();
		String dest = node.getDest();
		
		String line = formatLine(inst, src, dest);
		writeLine(line);
	}    
    
    @Override
    public void visit(LLReturn node) {
        // TODO Auto-generated method stub
        
        if (node.hasReturn()) {
            // mov %rsp, rax
        }
        
        // Write ret
    }


}
