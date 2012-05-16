package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;
import edu.mit.compilers.checker.Ir.IrBinOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CSE implements Optimization, LLNodeVisitor {
    private class DataflowBlock {
        public BasicBlock block;
        
        HashSet<String> gen;
        HashMap<String, String> genInverse;
        HashSet<String> kill;
        HashSet<String> in;
        HashSet<String> out;
        
    }
    
	ArrayList<ArrayList<LLAssign>> IN, OUT, GEN, KILL;
	ArrayList<LLAssign> currentGen, currentKill;
	
	HashSet<String> allPossibleExprs;
	HashSet<Integer> checkedBlocks;
	HashSet<BasicBlock> reachableBlocks;
	
	HashMap<String, String> exprToTemp;

	/*
	    Notes.
	  
	    0. initialize parents of each basic block belonging to method
        1. generate set of all possible exprs (RHS).
           decide on some rep. reuse LLAssign?
        2. implement gen kill:
           GEN(Integer, <representation of available expr>) = GEN[i]
           etc.
        3. make sure to reuse the same temp for the same expr!
           global list of exprs->temps (calculated from step 1)
        4. each basic block will have a set of available expressions
        5. if expr is available, replace LLAssign with two LLAssigns
        
        ps. dead code elimination will obviously help!
	*/
	
	@Override
	public void optimize(BasicBlock method) {
	    // TODO: step 0 beforehand.
	    // need the list of all basic blocks in the file.
	    // use block.getNum() as an array index to update blocks.
	    // this process should only need to be done once.
	    
	    allPossibleExprs = new HashSet<String>();
	    checkedBlocks = new HashSet<Integer>();
	    reachableBlocks = new HashSet<BasicBlock>();
	    collectExprs(method);
	    
	    
	    
	    
		for (LLNode instr : method.getInstructions()) {
			instr.accept(this);
		}
	}

	private void collectExprs(BasicBlock b) {
	    Integer id = b.getNum();
	    // check each block at most once to prevent infinite loops.
	    if (checkedBlocks.contains(id)) {
	        return;
	    }
	    checkedBlocks.add(id);
	    reachableBlocks.add(b);
	    
	    // check the block contents.
        for (LLNode instr : b.getInstructions()) {
            if (instr instanceof LLAssign) {
                extractExpr((LLAssign)instr);
            }
        }
        // then check the block successors.
        for (BasicBlock s : b.getChildren()) {
            collectExprs(s);
        }
	}
	
	// returns true iff allPossibleExprs was modified.
	private boolean extractExpr(LLAssign a) {
	    LLExpression rhs = a.getExpr();
	    
	    if (rhs instanceof LLBinaryOp) {
	        LLBinaryOp binExpr = (LLBinaryOp)rhs;
	        
	        String op;
	        switch (binExpr.getOp()) {
	        case PLUS:
	            op = "+";
	            break;
	        case MINUS:
	            op = "-";
	            break;
	        case MUL:
	            op = "*";
	            break;
	        case DIV:
	            op = "/";
	            break;
	        case MOD:
	            op = "%";
	            break;
            default:
                return false; // ignore non-arithmetic expressions for now.
	        }
	        
	        LLExpression left = binExpr.getLhs();
	        LLExpression right = binExpr.getRhs();
	        
	        String key = genExpressionRep(left, op, right);
	        if (key != null && !allPossibleExprs.contains(key)) {
	            allPossibleExprs.add(key);
	            return true;
	        }
	        return false;
	    }
	    
	    return false;
	}
	
	private String genExpressionRep(LLExpression left, String op, LLExpression right) {
	    String leftRep = null, rightRep = null;
	    
        if (left instanceof LLVarLocation) {
            leftRep = ((LLVarLocation)left).getLabel();
        } else if (left instanceof LLArrayLocation) {
            LLArrayLocation loc = (LLArrayLocation)left;
            String arrayLabel = loc.getLabel();
            String indexLabel;
            LLExpression indexExpr = loc.getIndexExpr();
            if (indexExpr instanceof LLVarLocation) {
                indexLabel = ((LLVarLocation)indexExpr).getLabel();
                leftRep = arrayLabel + "[" + indexLabel + "]";
            } else {
                // throw some error
            }
        } else if (left instanceof LLIntLiteral) {
            leftRep = String.valueOf(((LLIntLiteral)left).getValue());
        } else {
            // throw some error.
        }
	    
        if (right instanceof LLVarLocation) {
            rightRep = ((LLVarLocation)right).getLabel();
        } else if (right instanceof LLArrayLocation) {
            LLArrayLocation loc = (LLArrayLocation)right;
            String arrayLabel = loc.getLabel();
            String indexLabel;
            LLExpression indexExpr = loc.getIndexExpr();
            if (indexExpr instanceof LLVarLocation) {
                indexLabel = ((LLVarLocation)indexExpr).getLabel();
                rightRep = arrayLabel + "[" + indexLabel + "]";
            } else {
                // throw some error
            }
        } else if (right instanceof LLIntLiteral) {
            rightRep = String.valueOf(((LLIntLiteral)right).getValue());
        } else {
            // throw some error.
        }
        
        if (leftRep != null && rightRep != null) {
            return leftRep + op + rightRep;
        }
        
	    return null;
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
