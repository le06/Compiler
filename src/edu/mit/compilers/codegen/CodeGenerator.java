package edu.mit.compilers.codegen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import edu.mit.compilers.checker.Ir.IrBinOperator;

import edu.mit.compilers.codegen.ll.LLCmp;
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
import edu.mit.compilers.codegen.ll.LLExpression.Type;
import edu.mit.compilers.codegen.ll.LLFile;
import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNodeVisitor;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLNop;
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
    private final int OFFSET = 8;
    private final String RBP = "(%rbp)";	
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
    private LLLabel div_by_zero_label;
    
    private boolean printString = false;
    
    public CodeGenerator() {

    }
    
    // entry point for LL tree walker.
    public void outputASM(Writer output, LLFile decafFile) {
    	tab_level = 0;
    	array_oob_label = decafFile.getArrayOobLabel();
    	missing_return_label = decafFile.getMissingReturnLabel();
    	div_by_zero_label = decafFile.getDivByZeroLabel();
    	
        outputStream = output;
        decafFile.accept(this);
    }
    
    /*
     * ASM run-time error methods.
     */
    private void error_array_oob(LLFile node) {
    	array_oob_label.accept(this);
    	
    	tab_level++;    	
        LLCallout print_error = node.getArrayOobCallout();
        print_error.accept(this);
    	writeASMExit(1);
    	tab_level--;
    }
    
    private void error_missing_return(LLFile node) {
    	missing_return_label.accept(this);
    	
    	tab_level++;    	
        LLCallout print_error = node.getMissingReturnCallout();
        print_error.accept(this);
    	writeASMExit(2);
    	tab_level--;
    }
    
    private void error_div_by_zero(LLFile node) {
    	div_by_zero_label.accept(this);
    	
    	tab_level++;    	
        LLCallout print_error = node.getDivByZeroCallout();
        print_error.accept(this);
    	writeASMExit(3);
    	tab_level--;
    }
    
    private void writeASMExit(int error) {
    	// system call for exit.
    	LLMov mov_exit = new LLMov("$1", RAX);
    	mov_exit.accept(this);
    	
    	// interrupt to kernel.
    	String inst = "int";
    	String inst_line = formatLine(inst, "$0x80");
    	writeLine(inst_line);
    	
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
        
        error_array_oob(node);
        error_missing_return(node);
        error_div_by_zero(node);
        
        printString = true;
        for (LLStringLiteral l : node.getStringLiterals()) {
        	l.accept(this);
        }
        printString = false;
        
    }
    
    @Override
    public void visit(LLGlobalDecl node) {
    	tab_level++;
    	writeLine(".data");
    	tab_level--;
    	
        node.getLabel().accept(this);
        
        tab_level++;
        node.getMalloc().accept(this);
        tab_level--;
    }
    
    @Override
    public void visit(LLArrayDecl node) {
    	tab_level++;
    	writeLine(".data");
    	tab_level--;
    	
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
		tab_level++;
		
		// calling convention: need to add an enter instruction.
		String enter_inst = "enter";
		int num_temps = node.getNumTemps();
		String enter_arg1 = "$(8 * " + String.valueOf(num_temps) + ")";
		String enter_arg2 = "$0";
		String enter_line = formatLine(enter_inst, enter_arg1, enter_arg2);
		writeLine(enter_line);
	
		int num_params = node.getNumArgs();
		for (int i = 0; i < num_params; i++) {
			loadArgument(i, i+1);
		}
		
		// check the method code.
		node.getEnv().accept(this);
		
		Type type = node.getType();
		if (type == Type.VOID) {
			LLReturn default_return = new LLReturn();
			default_return.accept(this);			
		} else {
			// if a return statement is not encountered on a non-void
			// method, generate a run-time exception and terminate.
	        LLJump error = new LLJump(LLJump.JumpType.UNCONDITIONAL,
	        						  missing_return_label);
	        error.accept(this);
		}
		
		tab_level--;
	}
	
    private void loadArgument(int n, int offset) {
    	long actual_offset = offset*OFFSET;
    	String addr = "-" + String.valueOf(actual_offset) + RBP;
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
    		int arg_offset = (n-6)+2;
    		long actual_arg_offset = arg_offset*OFFSET;
    		String arg_addr = String.valueOf(actual_arg_offset) + RBP;
    		
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
		
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }

    }    
    
///////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void visit(LLAssign node) {
    	LLLocation loc = node.getLoc();
    	LLExpression expr = node.getExpr();
        
    	loc.accept(this);						// if array loc, need to check!
    	expr.accept(this);						// next, walk the expr.
    	String arg1 = expr.addressOfResult();	// temp memory location for evaluated expr.
    	String arg2 = loc.getLocation();
    	
    	if (loc instanceof LLArrayLocation) {
    		LLExpression index = ((LLArrayLocation)loc).getIndexExpr();
    		String index_val = index.addressOfResult();		// calculate index offset.
    		LLMov mov_index = new LLMov(index_val, R10);	// move offset to register.
    		mov_index.accept(this);							// write this in ASM.
    		
    		String mul_inst = "imul"; // change index into address offset.
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
    		String inst = "push"; // push arg onto stack!
    		String src = R10;
    		String push_line = formatLine(inst, src);
    		writeLine(push_line);
    		break;
    	}
    }
    
    @Override
    public void visit(LLMethodCall node) {
    	// visit each of the params in this expression.
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }
    	
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
    	// visit each of the params in this expression.
    	ArrayList<LLExpression> params = node.getParams();
        for (LLExpression p : params) {
            p.accept(this);
        }

    	// note the name of the callout function is NOT considered a param!
    	for (int i = params.size()-1; i >= 0; i--) {
    		pushArgument(i, params.get(i)); // push from RIGHT-TO-LEFT.
    	}
    	
    	// boilerplate instruction for callouts.
    	LLMov mov_rax = new LLMov("$0", RAX);
    	mov_rax.accept(this);
    	
    	String call_inst = "call";
    	String call_arg = node.getFnName();
    	call_arg = call_arg.substring(1, call_arg.length()-1);
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
        if (printString) {
            node.getLabel().accept(this);
            tab_level++;
            String directive = ".string";
            String arg = node.getText();
            String line = formatLine(directive, arg);
            writeLine(line);
            tab_level--;
        }
    }

///////////////////////////////////////////////////////////////////////////////    
    
    @Override
    public void visit(LLVarLocation node) {
    	boolean is_expr = false;
    	if (node.addressOfResult() != null) {
    		is_expr = true;
    	}
    	
    	if (is_expr) {
    		String src = node.getLocation();
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
    	cmp_line = formatLine(cmp_inst, R11, R10);		// dest (cmp) src.
    	writeLine(cmp_line);
    	LLJump jmp_if_less = new LLJump(LLJump.JumpType.LT,
    									array_oob_label);
    	jmp_if_less.accept(this);
    	
    	// check upper bounds.
    	long upper_bound = node.getSize()-1;
    	String upper_bound_operand = "$" + String.valueOf(upper_bound);
    	LLMov mov_upper_bound = new LLMov(upper_bound_operand, R11);
    	mov_upper_bound.accept(this);
    	cmp_line = formatLine(cmp_inst, R11, R10);
    	writeLine(cmp_line);
    	LLJump jmp_if_more = new LLJump(LLJump.JumpType.GT,
    									array_oob_label);
    	jmp_if_more.accept(this);
    	
    	boolean is_expr = false;
    	if (node.addressOfResult() != null) {
    		is_expr = true;
    	}
    	
    	if (is_expr) {
    		String mul_inst = "imul";	// change index into address offset.
    		String mul_val = "$8";	    // quadword size.
    		String mul_line = formatLine(mul_inst, mul_val, R10);
    		writeLine(mul_line);	 // write this in ASM.
    		
    		String src = node.getLocation(); // same as label.
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
    	
        // get both the lhs and rhs expressions, but don't walk them yet!
    	LLExpression left = node.getLhs();
    	LLExpression right = node.getRhs();
    	
    	LLMov mov_left, mov_right;
        
    	// mov instructions needed specifically for division.
    	LLMov mov_divisor, mov_zero, mov_dividend, mov_highbits;
    	LLJump div_error;
    	
    	IrBinOperator op = node.getOp();
    	String inst, inst_line;
    	LLMov mov_false, mov_true;
    	switch (op) { // define the result to be stored in %r10 no matter what.
    	// arithmetic ops.
    	case PLUS:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "add";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case MINUS:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "sub";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case MUL:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
    		inst = "imul";
    		inst_line = formatLine(inst, R11, R10);
    		writeLine(inst_line);
    		break;
    	case DIV:
        	left.accept(this);
        	right.accept(this);
    		
    		// are we dividing by zero? check the divisor.
    		mov_divisor = new LLMov(right.addressOfResult(), R10);
    		mov_zero = new LLMov("$0", R11);
    		mov_divisor.accept(this);
    		mov_zero.accept(this);
    		
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10); // dest (cmp) src.
        	writeLine(inst_line);
        	
        	div_error = new LLJump(LLJump.JumpType.EQUAL, div_by_zero_label);
        	div_error.accept(this);
    		
    		// move the dividend.
        	mov_dividend = new LLMov(left.addressOfResult(), RAX);
        	mov_dividend.accept(this);
        	
        	// make sure the higher-order 64 bits are filled correctly!
        	inst = "cmp";
        	inst_line = formatLine(inst, "$0", RAX); // dest (cmp) src.
        	writeLine(inst_line);
        	
        	// if non-negative, the higher-order bits should be zeroed.
        	mov_highbits = new LLMov("$0", RDX);
        	mov_highbits.accept(this);

        	// if negative, need to fill with 1-bits ("$-1") instead!
        	mov_highbits = new LLMov("$-1", R11);
        	mov_highbits.accept(this);
        	inst = "cmovl";
        	inst_line = formatLine(inst, R11, RDX);
        	writeLine(inst_line);
        	
        	// now perform the division.
        	// note that the divisor is already stored in %r10!
    		inst = "idiv";
    		inst_line = formatLine(inst, R10);
    		writeLine(inst_line);
    		
    		// move quotient from %rax to result register.
    		LLMov mov_quotient = new LLMov(RAX, R10);
    		mov_quotient.accept(this);
    		break;
    	case MOD:
        	left.accept(this);
        	right.accept(this);
    		
    		// are we dividing by zero? check the divisor.
    		mov_divisor = new LLMov(right.addressOfResult(), R10);
    		mov_zero = new LLMov("$0", R11);
    		mov_divisor.accept(this);
    		mov_zero.accept(this);
    		
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10); // dest (cmp) src.
        	writeLine(inst_line);
        	
        	div_error = new LLJump(LLJump.JumpType.EQUAL, div_by_zero_label);
        	div_error.accept(this);
    		
    		// move the dividend.
        	mov_dividend = new LLMov(left.addressOfResult(), RAX);
        	mov_dividend.accept(this);
        	
        	// make sure the higher-order 64 bits are filled correctly!
        	inst = "cmp";
        	inst_line = formatLine(inst, "$0", RAX); // dest (cmp) src.
        	writeLine(inst_line);
        	
        	// if non-negative, the higher-order bits should be zeroed.
        	mov_highbits = new LLMov("$0", RDX);
        	mov_highbits.accept(this);

        	// if negative, need to fill with 1-bits ("$-1") instead!
        	mov_highbits = new LLMov("$-1", R11);
        	mov_highbits.accept(this);
        	inst = "cmovl";
        	inst_line = formatLine(inst, R11, RDX);
        	writeLine(inst_line);
        	
        	// now perform the division.
        	// note that the divisor is already stored in %r10!
    		inst = "idiv";
    		inst_line = formatLine(inst, R10);
    		writeLine(inst_line);
    		
    		// move remainder from %rdx to result register.
    		LLMov mov_remainder = new LLMov(RDX, R10);
    		mov_remainder.accept(this);
    		break;
    		// arithmetic comparison operations.
    	case LT:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
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
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
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
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovge";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	case GT:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovg";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
		// cond ops. results get stored in the first operand!
    	case AND:
    		LLLabel short_circuit_label = node.getLabel();
    		LLJump short_circuit_jump;
    		if (short_circuit_label == null) { // should never be reached.
    	    	left.accept(this);
    	    	right.accept(this);
    			
            	mov_left = new LLMov(left.addressOfResult(), R10);
            	mov_right = new LLMov(right.addressOfResult(), R11);
            	mov_left.accept(this);
            	mov_right.accept(this);
            	
            	// compare operands.
            	inst = "and";
            	inst_line = formatLine(inst, R11, R10); // result gets stored into dest.
            	writeLine(inst_line);
            	break;
    		} else {
    	    	left.accept(this);
    			
    			mov_left = new LLMov(left.addressOfResult(), R10);
    			mov_right = new LLMov("$0", R11);
    			mov_left.accept(this);
    			mov_right.accept(this);
    			
            	inst = "cmp";
            	inst_line = formatLine(inst, R11, R10);
            	writeLine(inst_line);
            	
            	// if short circuiting, 0 (false) is already stored in R10!
            	short_circuit_jump = new LLJump(LLJump.JumpType.EQUAL, short_circuit_label);
    			short_circuit_jump.accept(this);
    			
    			// otherwise, AND equals the value of the right expr.
    			right.accept(this);
    			mov_right = new LLMov(right.addressOfResult(), R10);
    			mov_right.accept(this);
    			
    			// put the short circuit label after evaluation of the right expr.
    			short_circuit_label.accept(this);
    			
    			break;
    		}
    	case OR:
    		short_circuit_label = node.getLabel();
    		if (short_circuit_label == null) { // should never be reached.
    	    	left.accept(this);
    	    	right.accept(this);
    			
            	mov_left = new LLMov(left.addressOfResult(), R10);
            	mov_right = new LLMov(right.addressOfResult(), R11);
            	mov_left.accept(this);
            	mov_right.accept(this);
            	
            	// compare operands.
            	inst = "or";
            	inst_line = formatLine(inst, R11, R10); // result gets stored into dest.
            	writeLine(inst_line);
            	break;
    		} else {
    			left.accept(this);
    			
    			mov_left = new LLMov(left.addressOfResult(), R10);
    			mov_right = new LLMov("$1", R11);
    			mov_left.accept(this);
    			mov_right.accept(this);
    			
            	inst = "cmp";
            	inst_line = formatLine(inst, R11, R10);
            	writeLine(inst_line);
            	
            	// if short circuiting, 1 (true) is already stored in R10!
            	short_circuit_jump = new LLJump(LLJump.JumpType.EQUAL, short_circuit_label);
    			short_circuit_jump.accept(this);
    			
    			// otherwise, OR equals the value of the right expr.
    			right.accept(this);
    			mov_right = new LLMov(right.addressOfResult(), R10);
    			mov_right.accept(this);
    			
    			// put the short circuit label after evaluation of the right expr.
    			short_circuit_label.accept(this);
    			
    			break;
    		}
		// eq ops.
    	case EQ:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmove";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	case NEQ:
        	left.accept(this);
        	right.accept(this);
    		
        	mov_left = new LLMov(left.addressOfResult(), R10);
        	mov_right = new LLMov(right.addressOfResult(), R11);
        	mov_left.accept(this);
        	mov_right.accept(this);
        	
        	inst = "cmp";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	
        	mov_false = new LLMov("$0", R10);
        	mov_true = new LLMov("$1", R11);
        	mov_false.accept(this);
        	mov_true.accept(this);
        	
        	inst = "cmovne";
        	inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	break;
    	}
    	
    	String addr = node.addressOfResult();
    	LLMov mov_result = new LLMov(R10, addr);
    	mov_result.accept(this);    	
    }

    @Override
    public void visit(LLUnaryNeg node) {
    	// walk the negated expression.
        node.getExpr().accept(this);
    	
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
    	// walk the negated expression.
        node.getExpr().accept(this);
    	
        // move value of expr to register.
        LLExpression expr = node.getExpr();
        String expr_addr = expr.addressOfResult();
        LLMov mov_expr = new LLMov(expr_addr, R10);
        mov_expr.accept(this);
        
        // negate only the 1st bit.
        String inst = "xor";
        String inst_line = formatLine(inst, "$1", R10);
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
    	LLExpression cond = node.getCond();
    	
    	// for backwards compatibility with previous code.
    	// this assumes the cmp flags have already been set!
    	//
    	// LLJumps generated in this class should ignore cond, otherwise
    	// R10 and R11 get modified!
    	if (cond == null) {
        	LLLabel loc = node.getLabel();    	
        	String jmp_inst = node.getOpcode();
        	if (jmp_inst != null) { // should never be null.
        		String label = "." + loc.getName();
        		String jmp_line = formatLine(jmp_inst, label);
        		writeLine(jmp_line);
        	}    
    		
    	} else { // else, eval the expr and check its truth value
    		cond.accept(this);
    		String expr_addr = cond.addressOfResult();
    		
    		// load value of cond into reg
    		LLMov mov_cond = new LLMov(expr_addr, R10);
    		LLMov mov_true = new LLMov("$1", R11);
    		
    		// write to ASM.
    		mov_cond.accept(this);
    		mov_true.accept(this);
    		
    		// check if true.
        	String inst = "cmp";
        	String inst_line = formatLine(inst, R11, R10);
        	writeLine(inst_line);
        	
        	LLJump jmp;
        	if (node.getJumpValue() == true) {
        		jmp = new LLJump(LLJump.JumpType.EQUAL, node.getLabel());
        	} else {
        		jmp = new LLJump(LLJump.JumpType.NOT_EQUAL, node.getLabel());
        	}
    		jmp.accept(this);
        	
    	}
    }

    @Override
    public void visit(LLLabel node) {
    	if (tab_level <= 0) {
        	String name = node.getASMLabel();
        	writeLine(name);
    	} else {
    		tab_level--;
        	String name = node.getASMLabel();
        	writeLine(name);
    		tab_level++;
    	}
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
        	// walk the return expr.
        	LLExpression expr = node.getExpr();
        	expr.accept(this);
        	
        	String addr = expr.addressOfResult();
        	mov = new LLMov(addr, RAX);
        } else {
        	mov = new LLMov("$0", RAX);
        }
        
        mov.accept(this); // push stuff to RAX.
        writeLine("leave"); // and return!
        writeLine("ret");
    }

    @Override
    public void visit(LLNop node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLCmp node) {
        // TODO Auto-generated method stub
        
    }


}
