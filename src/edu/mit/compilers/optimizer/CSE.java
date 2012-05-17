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
        
        public DataflowBlock(BasicBlock block) {
            this.block = block;
            
            gen = new HashSet<String>();
            kill = new HashSet<String>();
            in = new HashSet<String>();
            out = new HashSet<String>();
        }
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
	    //         simultaneously calculate GEN and KILL for each dataflow block.
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
	    
	    Integer entry = method.getNum();
	    // step 3: initialize OUTs and changed.
	    HashSet<Integer> changed = new HashSet<Integer>();
	    for (DataflowBlock b : dataflowBlocks.values()) {
            if (b.block.getNum() == entry) { // gen[n].
                b.out = new HashSet<String>(b.gen);

            } else { // E - kill[n].
                changed.add(b.block.getNum());
                
                b.out = new HashSet<String>(allPossibleExprs);
                for (String k : b.kill) {
                    killSet(b.out, k);
                }
            }
	    }
	    
	    HashSet<String> in;
	    // step 4: start the gen-kill algorithm.
	    while (!changed.isEmpty()) {
	        Integer i = changed.iterator().next();
	        changed.remove(i);
	        DataflowBlock b = dataflowBlocks.get(i);

	        // merge point operation is intersection.
	        in = new HashSet<String>(allPossibleExprs);
	        for (BasicBlock parent : b.block.getParents()) {
	            Integer parentId = parent.getNum();
	            in.retainAll(dataflowBlocks.get(parentId).out);
	        }
	        
	        // IN - KILL
	        for (String k : b.kill) {
	            killSet(in, k);
	        }
	        
	        in.addAll(b.gen);
	        HashSet<String> newOut = new HashSet<String>(in);
	        if (!newOut.equals(b.out)) {
	            b.out = newOut;
	            for (BasicBlock child : b.block.getChildren()) {
	                changed.add(child.getNum());
	            }
	        }
	    }
	    
	    // step 5: eliminate CSEs based on available expressions.
	    
	}
	
	private void collectExprs(BasicBlock b) {
	    Integer id = b.getNum();
	    // check each block at most once to prevent infinite loops.
	    if (checkedBlocks.contains(id)) {
	        return;
	    }
	    checkedBlocks.add(id);
	    DataflowBlock newB = new DataflowBlock(b);
	    dataflowBlocks.put(id, newB);
	    
	    // check the block contents.
        for (LLNode instr : b.getInstructions()) {
            if (instr instanceof LLAssign) {
                processExpr((LLAssign)instr, newB);
            }
        }
        // then check the block successors.
        for (BasicBlock s : b.getChildren()) {
            collectExprs(s);
        }
	}
	
	// returns true if allPossibleExprs or allPossibleLocs is modified.
	private void processExpr(LLAssign a, DataflowBlock b) {
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
                return; // ignore non-arithmetic expressions for now.
	        }
	        
	        LLExpression left = binExpr.getLhs();
	        LLExpression right = binExpr.getRhs();
	        
	        String leftRep = genExpressionRep(left);
	        String rightRep = genExpressionRep(right);
	        
	        if (leftRep == null || rightRep == null) {
	            return;
	        }
	        
	        String key = leftRep + op + rightRep;
	        if (key != null) {
	            String loc = a.getLoc().getLabel();
	            allPossibleExprs.add(key);
	            allPossibleLocs.add(loc);
	            // update KILL/GEN.
	            killSet(b.gen, loc);
	            b.kill.add(loc);
	            // add the expr to GEN.
	            b.gen.add(key);
	        }

	    }
	    
	    return;
	}
	
	private void killSet(HashSet<String> set, String loc) {
	    for (String key : set) {
	        if (locInExpr(loc, key)) {
	            set.remove(key);
	        }
	    }
	}
	
	private boolean locInExpr(String loc, String expr) {
	    String opRegex = "[+-*/%]";
	    
	    String[] operands = expr.split(opRegex);
	    for (String s : operands) {
	        if (s.equals(loc)) {
	            return true;
	        }
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
