package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;
import edu.mit.compilers.codegen.ll.*;
import edu.mit.compilers.checker.Ir.IrBinOperator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CSE implements Optimization, LLNodeVisitor {
    private class DataflowBlock {
        BasicBlock block;
        
        HashSet<String> gen;
        HashSet<String> kill;
        HashSet<String> in;
        HashSet<String> out;
        
    }
    
	ArrayList<ArrayList<LLAssign>> IN, OUT, GEN, KILL;
	ArrayList<LLAssign> currentGen, currentKill;
	
	HashSet<String> allPossibleExprs;
	HashSet<String> allPossibleLocs;
	HashSet<Integer> checkedBlocks;
	HashMap<Integer, DataflowBlock> dataflowBlocks;
	
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
	    
	    // step 1: collect all CSE exprs and the locs these exprs are assigned to.
	    allPossibleExprs = new HashSet<String>();
	    allPossibleLocs = new HashSet<String>();
	    checkedBlocks = new HashSet<Integer>();
	    dataflowBlocks = new HashMap<Integer, DataflowBlock>();
	    collectExprs(method);
	    
	    // step 2: map each expr to a temp, which will be used every time the expr is available.
	    int counter = 1;
	    String temp_prefix = "new_t";
	    String temp_name = temp_prefix + counter;
	    for (String key : allPossibleExprs) {
	        while (allPossibleLocs.contains(temp_name)) {
	            counter++;
	            temp_name = temp_prefix + counter;
	        }
	        exprToTemp.put(key, temp_name);
	        counter++;
	    }
	    
	    // step 3: initialize gen-kill data structures.
	    for (DataflowBlock b : dataflowBlocks.values()) {
	        // TODO: put stuff here
	    }
	    
	    // step 4: start the gen-kill algorithm.
	    // step 5: eliminate CSEs based on available expressions.
	    
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
	    DataflowBlock newB = new DataflowBlock();
	    newB.block = b;
	    dataflowBlocks.put(id, newB);
	    
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
	
	// returns true if allPossibleExprs or allPossibleLocs is modified.
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
	        
	        String leftRep = genExpressionRep(left);
	        String rightRep = genExpressionRep(right);
	        
	        if (leftRep == null || rightRep == null) {
	            return false;
	        }
	        
	        String key = leftRep + op + rightRep;
	        if (key != null) {
	            boolean modified = false;
	            String loc = a.getLoc().getLabel();
	            modified |= allPossibleExprs.add(key);
	            modified |= allPossibleLocs.add(loc);
	            return modified;
	        }
	        return false;
	    }
	    
	    return false;
	}
	
	private String genExpressionRep(LLExpression expr) {
	    String rep = null;
	    
        if (expr instanceof LLVarLocation) {
            rep = ((LLVarLocation)expr).getLabel();
        } else if (expr instanceof LLArrayLocation) {
            LLArrayLocation loc = (LLArrayLocation)expr;
            String arrayLabel = loc.getLabel();
            String indexLabel;
            LLExpression indexExpr = loc.getIndexExpr();
            if (indexExpr instanceof LLVarLocation) {
                indexLabel = ((LLVarLocation)indexExpr).getLabel();
                rep = arrayLabel + "[" + indexLabel + "]";
            } else {
                // throw some error
            }
        } else if (expr instanceof LLIntLiteral) {
            rep = String.valueOf(((LLIntLiteral)expr).getValue());
        } else {
            // throw some error.
        }
        
        return rep;
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
