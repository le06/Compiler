package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import edu.mit.compilers.checker.Ir.IrBinOperator;

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
	private final String R8  = "%r8";
	private final String R9  = "%r9";
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
    
    private LLLabel array_oob_label;
    private LLLabel missing_return_label;
    
    public CodeGenerator() {

    }
    
    // entry point for LL tree walker.
    public void outputASM(Writer output, LLFile decafFile) {
    	tab_level = 0;
    	array_oob_label = decafFile.getArrayOobLabel();
    	missing_return_label = decafFile.getMissingReturnLabel();
    	
        outputStream = output;
        decafFile.accept(this);
    }
    
    /*
     * ASM run-time error methods.
     */
    private void error_array_oob() {
    	// TODO: fill in ASM
    }
    
    private void error_missing_return() {
    	// TODO: fill in ASM
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
        
        error_array_oob();
        error_missing_return();
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
		String label = node.getName() + ":";
		writeLine(label);
		// node accept function goes to node environment next.
	}
	
    @Override
    public void visit(LLEnvironment node) {
		tab_level++;
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }
        
        // TODO: LLMethodDecl need information about return type!
        
        //LLJump error = new LLJump(LLJump.JumpType.UNCONDITIONAL,
        //						  missing_return_label);
        //error.accept(this);
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
    		mov_index.accept(this);							// write this inType ASM.
    		
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
    	// polymorphic, so we don't have to check if arg is
    	// a StringLiteral or not.
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
    	for (int i = params.size()-1; i >= 0; i--) {
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
    	// arg exprs are evaluated in accept(). read those values into regs!
    	ArrayList<LLExpression> params = node.getParams();
    	for (int i = params.size()-1; i >= 0; i--) {
    		pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
    	}
    	
    	// boilerplate instruction for callouts.
    	LLMov mov_rax = new LLMov("$0", RAX);
    	mov_rax.accept(this);
    	
    	String call_inst = "call";
    	String call_arg = node.getFnName();
    	String call_line = formatLine(call_inst, call_arg);
    	writeLine(call_line);
    	
    	String expr_addr = node.addressOfResult();
    	if (expr_addr != null) {
    		LLMov mov_result = new LLMov(RAX, expr_addr);
    		mov_result.accept(this);
    	}
    }
    
    @Override
    public void visit(LLStringLiteral node) {
    	tab_level++;
    	String directive = ".string";
    	String arg = node.getText();
    	String line = formatLine(directive, arg);
    	writeLine(line);
    	tab_level--;
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
    	
		String index_val = index.addressOfResult();		// calculate value of index.
		LLMov mov_index = new LLMov(index_val, R10);	// move index to register.
		mov_index.accept(this);							// write this in ASM.

		String cmp_inst = "cmp";
		String cmp_line;
		// check lower bounds.
    	LLMov mov_lower_bound = new LLMov("$0", R11);
    	mov_lower_bound.accept(this);
    	cmp_line = formatLine(cmp_inst, R10, R11);
    	writeLine(cmp_line);
    	LLJump jmp_if_less = new LLJump(LLJump.JumpType.LESS_THAN,
    									array_oob_label);
    	jmp_if_less.accept(this);
    	
    	// check upper bounds.
    	long upper_bound = node.getSize()-1;
    	String upper_bound_operand = "$" + String.valueOf(upper_bound);
    	LLMov mov_upper_bound = new LLMov(upper_bound_operand, R11);
    	mov_upper_bound.accept(this);
    	cmp_line = formatLine(cmp_inst, R10, R11);
    	writeLine(cmp_line);
    	LLJump jmp_if_more = new LLJump(LLJump.JumpType.MORE_THAN,
    									array_oob_label);
    	jmp_if_more.accept(this);
    	
    	boolean is_expr = false;
    	if (node.addressOfResult() != null) {
    		is_expr = true;
    	}
    	
    	if (is_expr) {
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
        // load the lhs and rhs into registers.
    	LLExpression left = node.getLhs();
    	LLExpression right = node.getRhs();
    	LLMov mov_left, mov_right;
        
    	IrBinOperator op = node.getOp();
    	String inst, inst_line;
    	LLMov mov_false, mov_true;
    	switch (op) { // define the result to be stored in %r10 no matter what.
    	// arithmetic ops.
    	case PLUS:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "add";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case MINUS:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "sub";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case MUL:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "imul";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case DIV:
        	mov_left = new LLMov(left.addressOfResult(), RDX);
        	mov_right = new LLMov(right.addressOfResult(), RAX);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "idiv";
    		inst_line = formatLine(inst, RDX, RAX);
    		writeLine(inst_line);
    		
    		// move quotient from %rax to result register.
    		LLMov mov_quotient = new LLMov(RAX, R10);
    		mov_quotient.accept(this);
    		break;
    	case MOD:
        	mov_left = new LLMov(left.addressOfResult(), RDX);
        	mov_right = new LLMov(right.addressOfResult(), RAX);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "idiv";
    		inst_line = formatLine(inst, RDX, RAX);
    		writeLine(inst_line);
    		
    		// move remainder from %rax to result register.
    		LLMov mov_remainder = new LLMov(RDX, R10);
    		mov_remainder.accept(this);
    		break;
    		// arithmetic comparison operations.
    	case LT:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovl";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	case LEQ:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovle";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	case GEQ:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovg";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	case GT:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovge";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
		// cond ops. results get stored in the first operand!
    	case AND:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	// compare operands.
        	inst = "and";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
    	case OR:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	// compare operands.
        	inst = "or";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
		// eq ops.
    	case EQ:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmove";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
    	case NEQ:
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R10, R11);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovne";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
    	}
    	
    	String addr = node.addressOfResult();
    	LLMov mov_result = new LLMov(R10, addr);
    	mov_result.accept(this);    	
    }

    @Override
    public void visit(LLUnaryNeg node) {
        // move value of expr to register.
        LLExpression expr = node.getExpr();
        String expr_addr = expr.addressOfResult();
        LLMov mov_expr = new LLMov(expr_addr, R10);
        mov_expr.accept(this);
        
        // perform 2s-complement negation.
        String inst = "neg";
        String inst_line = formatLine(inst, R10);
        writeLine(inst_line);
        
        String result_addr = node.addressOfResult();
        LLMov mov_result = new LLMov(R10, result_addr);
        mov_result.accept(this);
    }

    @Override
    public void visit(LLUnaryNot node) {
        // move value of expr to register.
        LLExpression expr = node.getExpr();
        String expr_addr = expr.addressOfResult();
        LLMov mov_expr = new LLMov(expr_addr, R10);
        mov_expr.accept(this);
        
        // negate only the 1st bit.
        String inst = "xor";
        String inst_line = formatLine(inst, R10, "$1");
        writeLine(inst_line);
        
        String result_addr = node.addressOfResult();
        LLMov mov_result = new LLMov(R10, result_addr);
        mov_result.accept(this);
    }
    
    @Override
    public void visit(LLBoolLiteral node) {
        LLMov mov_literal;
        if (node.getValue() == true) {
        	mov_literal = new LLMov("$1", R10);
        } else {
        	mov_literal = new LLMov("$0", R10);
        }
        mov_literal.accept(this);
        
        String result_addr = node.addressOfResult();
        LLMov mov_result = new LLMov(R10, result_addr);
        mov_result.accept(this);
    }

    @Override
    public void visit(LLIntLiteral node) {
        // generate immediate operand.
        long value = node.getValue();
        String value_operand = "$" + String.valueOf(value);
        LLMov mov_literal = new LLMov(value_operand, R10);
        mov_literal.accept(this);
        
        String result_addr = node.addressOfResult();
        LLMov mov_result = new LLMov(R10, result_addr);
        mov_result.accept(this);
    }

///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLJump node) {
    	LLLabel loc = node.getLabel();    	
    	String jmp_inst = node.getOpcode();
    	if (jmp_inst != null) {
    		String label = "." + loc.getName();
    		String jmp_line = formatLine(jmp_inst, label);
    		writeLine(jmp_line);
    	}    	
    }

    @Override
    public void visit(LLLabel node) {
    	String name = node.getASMLabel();
    	writeLine(name);
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
    	LLMov mov = null;
        if (node.hasReturn()) {
        	LLExpression expr = node.getExpr();
        	String addr = expr.addressOfResult();
        	mov = new LLMov(addr, RAX);
        }
        
        if (mov != null) {
        	mov.accept(this);
        }
        writeLine("leave");
        writeLine("ret");
    }


}
