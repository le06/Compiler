package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;
import java.util.ArrayList;
import java.util.HashSet;

public class CSE implements Optimization, LLNodeVisitor {
	ArrayList<ArrayList<LLAssign>> IN, OUT, GEN, KILL;
	ArrayList<LLAssign> currentGen, currentKill;
	
	@Override
	public void optimize(BasicBlock method) {
		for (LLNode instr : method.getInstructions()) {
			instr.accept(this);
		}
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
	
	public boolean kills(LLAssign current, LLAssign past) {
		if (current.getLoc() instanceof LLVarLocation) {
			if (!(past.getLoc() instanceof LLVarLocation)) {
				return false;
			} else {
				return ((LLVarLocation)current.getLoc()).addressOfResult()
				                     .equals(
				       ((LLVarLocation)past.getLoc()).addressOfResult());
			}
		} else if (current.getLoc() instanceof LLArrayLocation) {
			if (!(past.getLoc() instanceof LLArrayLocation)) {
				return false;
			} else {
				LLArrayLocation c = ((LLArrayLocation)current.getLoc());
				LLArrayLocation p = ((LLArrayLocation)past.getLoc());
				boolean same =  c.getLabel()
				                     .equals(
				                p.getLabel());
				
				if (c.getIndexExpr() instanceof LLVarLocation) {
					return same && (((LLVarLocation)c.getIndexExpr()).addressOfResult()
                            				.equals(
                            		((LLVarLocation)p.getIndexExpr()).addressOfResult()));
				} else if (c.getIndexExpr() instanceof LLIntLiteral) {
					return same && (((LLIntLiteral)c.getIndexExpr()).getValue()
					                                  ==
								   ((LLIntLiteral)p.getIndexExpr()).getValue());
				} else {
					throw new RuntimeException("Unexpected array index type");
				}                 
			}
		} else {
			throw new RuntimeException("Unexpected assigned type");
		}
	}
}
