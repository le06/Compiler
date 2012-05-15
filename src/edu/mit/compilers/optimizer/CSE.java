package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;
import java.util.ArrayList;
import java.util.HashSet;

public class CSE implements Optimization, LLNodeVisitor {
	ArrayList<HashSet<LLExpression>> IN, OUT, GEN, KILL;
	
	@Override
	public void optimize(BasicBlock method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLFile node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLGlobalDecl node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLArrayDecl node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMalloc node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMethodDecl node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLEnvironment node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLAssign node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMethodCall node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLCallout node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLStringLiteral node) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void visit(LLJump node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLLabel node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLMov node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLCmp node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLReturn node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LLNop node) {
		// TODO Auto-generated method stub
		
	}
	
}
