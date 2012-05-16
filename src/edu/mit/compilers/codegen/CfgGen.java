package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.checker.Ir.IrBinOperator;
import edu.mit.compilers.codegen.ll.*;
import edu.mit.compilers.codegen.ll.LLExpression.Type;
import edu.mit.compilers.codegen.ll.LLJump.JumpType;
import edu.mit.compilers.codegen.ll.LLCmp;

public class CfgGen implements LLNodeVisitor {
    private BasicBlock currentBlock, head;
    private ArrayList<LLNode> instructions;
    private String TEMP = "t";
    private int currentTemp = 0;
    
    private String SS = "short_circuit";
    private int currentSS = 0;
    
    private ArrayList<BasicBlock> blockCache;
    private ArrayList<String> targetCache;
    
    private HashMap<String, BasicBlock> blockTable;
    
    private ArrayList<BasicBlock> blocksInOrder;
    
    public void generateCFG(LLFile file) {
        instructions = new ArrayList<LLNode>();
        blockTable = new HashMap<String, BasicBlock>();
        
        blockCache = new ArrayList<BasicBlock>();
        targetCache = new ArrayList<String>();
        
        blocksInOrder = new ArrayList<BasicBlock>();
        
        currentBlock = new BasicBlock();
        blocksInOrder.add(currentBlock);
        head = currentBlock;
        
        file.accept(this);
        
        
        
        // Apply relationships in cache
        for (int i = 0; i < blockCache.size(); i++) {
            blockCache.get(i)
                     .addChild(
                          blockTable.get(targetCache.get(i)));
        }
    }
    
    public BasicBlock getCFG() {
        return head;
    }
    
    public ArrayList<BasicBlock> getBlocksInOrder() {
    	return blocksInOrder;
    }
    
    public ArrayList<LLNode> getInstructions() {
        return instructions;
    }

    private String getNextTemp() {
        return TEMP + currentTemp++;
    }
    
    private String getNextSS() {
    	return SS + currentSS++;
    }
    
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
            
            if (m.getType() == Type.VOID) {
        		LLReturn ret = new LLReturn(); // Add return 0 to void method
        		currentBlock.addInstruction(ret);
                instructions.add(ret);
        	}
        }

        node.getMain().accept(this);
        LLReturn ret = new LLReturn(); // Add return 0 to void method
		currentBlock.addInstruction(ret);
        instructions.add(ret);
        
        /*error_array_oob(node);
        error_missing_return(node);
        error_div_by_zero(node);*/
        
        for (LLStringLiteral l : node.getStringLiterals()) {
            l.accept(this);
        }
    }

    @Override
    public void visit(LLGlobalDecl node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLArrayDecl node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLMalloc node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLMethodDecl node) {
        currentBlock = new BasicBlock();
        blocksInOrder.add(currentBlock);
        head.addChild(currentBlock);
        
        currentBlock.addInstruction(node);
        instructions.add(node);
        
        currentTemp = 0;
        
        // check the method code.
        node.getEnv().accept(this);
    }

    @Override
    public void visit(LLEnvironment node) {
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }
    }

    @Override
    public void visit(LLAssign node) {
        LLAssign newAss;
        
        if (!(node.getExpr().getType() == Type.BOOLEAN)) {
            newAss = new LLAssign(node.getLoc(), 
                                  reduceExpression(node.getExpr()));
        } else {
            newAss = new LLAssign(node.getLoc(), 
                                  reduceBooleanExpression(node.getExpr()));
        }
        
        instructions.add(newAss);
        currentBlock.addInstruction(newAss);
    }

    @Override
    public void visit(LLMethodCall node) {
        LLMethodCall newCall = new LLMethodCall(node.getMethodName(), 
                                                node.getType());
        
        // Get new arg locations: 
        for (LLExpression param : node.getParams()) {
            newCall.addParam(reduceExpression(param));
        }
        
        instructions.add(newCall);
        currentBlock.addInstruction(newCall);
    }

    @Override
    public void visit(LLCallout node) {
        LLCallout newCall = new LLCallout(node.getFnName());
        
        
        for (LLExpression p : node.getParams()) {
            if (p instanceof LLStringLiteral) {
                newCall.addParam(p);
            } else {
                newCall.addParam(reduceExpression(p));
            }
        }
        
        instructions.add(newCall);
        currentBlock.addInstruction(newCall);
    }

    @Override
    public void visit(LLStringLiteral node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLVarLocation node) {
        // Should not have to touch this
    }

    @Override
    public void visit(LLArrayLocation node) {
        LLArrayLocation out = new LLArrayLocation(node.getLabel(),
                                                  node.getSize(), 
                                                  reduceExpression(node.getIndexExpr()));
        
        instructions.add(out);
        currentBlock.addInstruction(out);
    }

    @Override
    public void visit(LLBinaryOp node) {
        // Do nothing - handled in upper level expressions
    }

    @Override
    public void visit(LLUnaryNeg node) {
        LLUnaryNeg out = new LLUnaryNeg(
                              reduceExpression(node));
        
        instructions.add(out);
        currentBlock.addInstruction(out);
    }

    @Override
    public void visit(LLUnaryNot node) {
        // Should not occur
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // Should not have to do anything
    }

    @Override
    public void visit(LLIntLiteral node) {
        // Should not have to do anything
    }

    private void writeCmp(LLBinaryOp op, boolean jmpIf, LLLabel target) {
    	LLCmp cmp;
    	LLJump jmp;
    	LLExpression l, r;
    	
    	if (op.getRhs().getType() == Type.BOOLEAN) {
    		r = reduceBooleanExpression(op.getRhs());
    	} else {
    		r = reduceExpression(op.getRhs());
    	}
    	
    	if (op.getLhs().getType() == Type.BOOLEAN) {
    		l = reduceBooleanExpression(op.getLhs());
    	} else {
    		l = reduceExpression(op.getLhs());
    	}
    	
    	cmp = new LLCmp(reduceExpression(r), 
    			        reduceExpression(l));
    	
    	switch (op.getOp()) {
    	case EQ:
            if (jmpIf) {
            	jmp = new LLJump(JumpType.EQUAL, target);
            } else {
            	jmp = new LLJump(JumpType.NOT_EQUAL, target);
            }
            break;
        case NEQ:
        	if (jmpIf) {
            	jmp = new LLJump(JumpType.NOT_EQUAL, target);
            } else {
            	jmp = new LLJump(JumpType.EQUAL, target);
            }
            break;
		case GEQ:
			if (jmpIf) {
				jmp = new LLJump(JumpType.GEQ, target);
			} else {
				jmp = new LLJump(JumpType.LT, target);
			}
			break;
		case GT:
			if (jmpIf) {
				jmp = new LLJump(JumpType.GT, target);
			} else {
				jmp = new LLJump(JumpType.LEQ, target);
			}
			break;
		case LEQ:
			if (jmpIf) {
				jmp = new LLJump(JumpType.LEQ, target);
			} else {
				jmp = new LLJump(JumpType.GT, target);
			}
			break;
		case LT:
			if (jmpIf) {
				jmp = new LLJump(JumpType.LT, target);
			} else {
				jmp = new LLJump(JumpType.GEQ, target);
			}
			break;
        default:
        	throw new RuntimeException("Shouldn't be here in writeCmp");
    	}
    	
        instructions.add(cmp);
        currentBlock.addInstruction(cmp);
        instructions.add(jmp);
        currentBlock.addInstruction(jmp);
        
        updateControlFlow(target);
    }
    
    private void reduceBooleanExpression(LLExpression cond, boolean jmpIf, LLLabel jmpPoint) {
        if (cond instanceof LLBinaryOp) {
        	LLBinaryOp op = (LLBinaryOp)cond;
        	switch (op.getOp()) {
        	case EQ:
            case NEQ:
            case GEQ:
            case GT:
            case LEQ:
            case LT:
            	writeCmp(op, jmpIf, jmpPoint);
                break;
            case AND:
            	// ReduceLeft
            	// If false, jump to ss
            	// ReduceRight
            	// If false, jump to ss
            	// jump jmpPoint
            	// .ss
            	LLLabel ssAnd = new LLLabel(getNextSS());
            	LLJump andSuccess = new LLJump(JumpType.UNCONDITIONAL, jmpPoint);
            	reduceBooleanExpression(op.getLhs(), false, ssAnd);
            	reduceBooleanExpression(op.getRhs(), false, ssAnd);
            	instructions.add(andSuccess);
            	currentBlock.addInstruction(andSuccess);
            	
            	updateControlFlow(jmpPoint);
            	
            	instructions.add(ssAnd);
            	currentBlock.addInstruction(ssAnd);
            	
            	ssAnd.accept(this);
            	break;
            case OR:
            	// ReduceLeft
            	// If true, jump to ss
            	// ReduceRight
            	// If true, jump to ss
            	// jump jmpPoint
            	// .ss
/*            	LLLabel ssOr = new LLLabel(getNextSS());
            	LLJump orSuccess = new LLJump(JumpType.UNCONDITIONAL, jmpPoint);*/
            	reduceBooleanExpression(op.getLhs(), true, jmpPoint);
            	reduceBooleanExpression(op.getRhs(), true, jmpPoint);
            	break;
        	}
        	
        } else if (cond instanceof LLVarLocation) {
        	LLCmp cmp = new LLCmp((LLVarLocation)cond);
        	LLJump jmp = new LLJump(JumpType.NOT_EQUAL, jmpPoint);
        	
        	instructions.add(cmp);
            currentBlock.addInstruction(cmp);
            instructions.add(jmp);
            currentBlock.addInstruction(jmp);
            
            updateControlFlow(jmpPoint);
        } else if (cond instanceof LLMethodCall) {
        	LLCmp cmp = new LLCmp((LLVarLocation)reduceExpression(cond));
        	LLJump jmp = new LLJump(JumpType.NOT_EQUAL, jmpPoint);
        	
        	instructions.add(cmp);
            currentBlock.addInstruction(cmp);
            instructions.add(jmp);
            currentBlock.addInstruction(jmp);
            
            updateControlFlow(jmpPoint);
        } else if (cond instanceof LLUnaryNot) { 
        	reduceBooleanExpression(((LLUnaryNot) cond).getExpr(), !jmpIf, jmpPoint);
        } else {
        	throw new RuntimeException("Unimplemented in CfgGen, reduceBoolean");
        }
    }
    
    public void updateControlFlow(LLLabel l) {
    	BasicBlock newBlock = new BasicBlock();
        // At a jump, the current basic block splits to have two children:
        // taking the jump and not taking the jump
        currentBlock.addChild(newBlock);

        // As the label may not be in the table yet, cache this relationship
        // and apply it at the end
        blockCache.add(currentBlock);
        targetCache.add(l.getName());
        
        // Following instructions are in own basic block
        currentBlock = newBlock; 
        blocksInOrder.add(currentBlock);
    }
    
    @Override
    public void visit(LLJump node) {
    	if (node.getCond() == null) {
    		instructions.add(node);
            currentBlock.addInstruction(node);
            
            updateControlFlow(node.getLabel());
    	} else {
    		reduceBooleanExpression(node.getCond(), true, node.getLabel());
    	}
    
        
        /*BasicBlock newBlock = new BasicBlock();
        // At a jump, the current basic block splits to have two children:
        // taking the jump and not taking the jump
        currentBlock.addChild(newBlock);

        // As the label may not be in the table yet, cache this relationship
        // and apply it at the end
        blockCache.add(currentBlock);
        targetCache.add(node.getLabel().getName());
        
        // Following instructions are in own basic block
        currentBlock = newBlock; 
        blocksInOrder.add(currentBlock);*/
    }

    @Override
    public void visit(LLLabel node) {
        BasicBlock newBlock = new BasicBlock();
        newBlock.addInstruction(node);
        instructions.add(node);
        
        blockTable.put(node.getName(), newBlock);
        
        currentBlock.addChild(newBlock);
        currentBlock = newBlock;
        blocksInOrder.add(currentBlock);
    }

    @Override
    public void visit(LLMov node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLReturn node) {
        if (node.hasReturn()) {
            if (node.getExpr().getType() == Type.INT) {
                LLReturn newRet = new LLReturn(reduceExpression(node.getExpr()));
                currentBlock.addInstruction(newRet);
                instructions.add(newRet);
            } else {
                LLReturn newRet = new LLReturn(reduceBooleanExpression(node.getExpr()));
                currentBlock.addInstruction(newRet);
                instructions.add(newRet);
            }
        } else {
            currentBlock.addInstruction(node);
            instructions.add(node);
        }
    }

    @Override
    public void visit(LLNop node) {
        // Skip this instruction
    }
    
    private LLExpression reduceExpression(LLExpression e) {
        if (e instanceof LLBinaryOp) {
            return reduceBinOp((LLBinaryOp)e);
        } else if (e instanceof LLMethodCall) {
            return reduceFnCall((LLMethodCall)e);
        } else if (e instanceof LLVarLocation) {
            return (LLVarLocation)e;
        } else if (e instanceof LLArrayLocation) {
            return reduceArrayLocation((LLArrayLocation)e);
        } else if (e instanceof LLIntLiteral) {
            return e;
        } else if (e instanceof LLBoolLiteral) {
            return e;
        } else if (e instanceof LLUnaryNeg) {
            return null;
        } else if (e instanceof LLCallout) {
        	return reduceCallout((LLCallout)e);
        } else if (e instanceof LLUnaryNot) { 
        	LLUnaryNot n = new LLUnaryNot(reduceBooleanExpression(((LLUnaryNot) e).getExpr()));
        	return n;
        } else {
            throw new RuntimeException("Illegal expr type in assign");
        }
    }
    
    private LLVarLocation reduceArrayLocation(LLArrayLocation l) {
        LLArrayLocation newA = new LLArrayLocation(l.getLabel(), 
                                                   l.getSize(), 
                                                   reduceExpression(l.getIndexExpr()));
        LLVarLocation out = new LLVarLocation(1, getNextTemp());
        
        LLAssign node = new LLAssign(out, newA);

        instructions.add(node);
        currentBlock.addInstruction(node);
        
        return out;
    }
    
    private LLVarLocation reduceCallout(LLCallout m) {
    	LLCallout newCall = new LLCallout(m.getFnName());
        
        
        for (LLExpression p : m.getParams()) {
            if (p instanceof LLStringLiteral) {
                newCall.addParam(p);
            } else {
                newCall.addParam(reduceExpression(p));
            }
        }
        
        LLVarLocation out = new LLVarLocation(1, getNextTemp());
        LLAssign ass = new LLAssign(out, newCall);
        
        instructions.add(ass);
        currentBlock.addInstruction(ass);
        
        return out;
    }
    
    private LLVarLocation reduceFnCall(LLMethodCall m) {
    	LLMethodCall m2 = new LLMethodCall(m.getMethodName(), m.getType());
    	
    	for (LLExpression param : m.getParams()) {
    		m2.addParam(reduceExpression(param));
    	}
    	
        LLVarLocation out = new LLVarLocation(1, getNextTemp());
        LLAssign ass = new LLAssign(out, m2);
        
        // Add this instruction to the output
        instructions.add(ass);
        currentBlock.addInstruction(ass);
        
        return out;
    }
    
    private LLVarLocation reduceUnaryNeg(LLUnaryNeg n) {
    	LLVarLocation out = new LLVarLocation(1, getNextTemp());
    	LLUnaryNeg neg = new LLUnaryNeg(reduceExpression(n.getExpr()));
        LLAssign ass = new LLAssign(out, neg);
        
        instructions.add(ass);
        currentBlock.addInstruction(ass);
        
        return out;
    }
    
    private LLExpression reduceBinOp(LLBinaryOp node) {
        LLExpression l, r; 
        LLVarLocation out;
        
        if (node.getLhs() instanceof LLBinaryOp) {
            l = reduceBinOp((LLBinaryOp)node.getLhs());
        } else if (node.getLhs() instanceof LLMethodCall) {
            l = reduceFnCall((LLMethodCall)node.getLhs());
        } else if (node.getLhs() instanceof LLVarLocation) {
            l = (LLVarLocation)node.getLhs();
        } else if (node.getLhs() instanceof LLIntLiteral) {
            l = node.getLhs();
        } else if (node.getLhs() instanceof LLUnaryNeg) {
        	l = reduceUnaryNeg((LLUnaryNeg)node.getLhs());
        } else {
            throw new RuntimeException("Unexpected type in bin op: " + node.getLhs().getClass());
        }
        
        if (node.getRhs() instanceof LLBinaryOp) {
            r = reduceBinOp((LLBinaryOp)node.getRhs());
        } else if (node.getRhs() instanceof LLMethodCall) {
            r = reduceFnCall((LLMethodCall)node.getRhs());
        } else if (node.getRhs() instanceof LLVarLocation) {
            r = (LLVarLocation)node.getRhs();
        } else if (node.getRhs() instanceof LLIntLiteral) {
            r = node.getRhs();
        } else if (node.getRhs() instanceof LLUnaryNeg) {
        	r = reduceUnaryNeg((LLUnaryNeg)node.getRhs());
        } else {
            throw new RuntimeException("Unexpected type in bin op: " + node.getRhs().getClass());
        }
        
        out = new LLVarLocation(1, getNextTemp());
        
        // out = l *op* r
        node.setLhs(l);
        node.setRhs(r);
        LLAssign ass = new LLAssign(out, node);
        
        // Add this instruction to the output
        instructions.add(ass);
        currentBlock.addInstruction(ass);
        
        return out;
    }
    
    @Override
	public void visit(LLCmp node) {
		// Do nothing
	}
    
/*    // Used in conditionals
    private void reduceBooleanExpression(LLExpression e, LLLabel jmpLoc) {
        if (e instanceof LLBinaryOp) {
        	
        } else if (e instanceof LLVarLocation) {
        	LLCmp cmp = new LLCmp((LLVarLocation)e);
        	LLJump jmp = new LLJump(JumpType.NOT_EQUAL, jmpLoc);
        	
        	instructions.add(cmp);
            currentBlock.addInstruction(cmp);
            instructions.add(jmp);
            currentBlock.addInstruction(jmp);
        }
    }*/
    
    // Used in statements
    private LLExpression reduceBooleanExpression(LLExpression e) {
    	if (e instanceof LLBinaryOp) {
        	LLBinaryOp b = (LLBinaryOp)e;
        	LLExpression l, r;
        	
        	switch (b.getOp()) {
        	case AND:
        	case OR:
        		l = reduceBooleanExpression(b.getLhs());
        		r = reduceBooleanExpression(b.getRhs());
        		break;
        	case EQ:
        	case NEQ:
        	case LT:
        	case GT:
        	case LEQ:
        	case GEQ:
        		l = reduceExpression(b.getLhs());
        		r = reduceExpression(b.getRhs());
        		break;
        	default:
        		throw new RuntimeException("Unexpected type in bool expr");
        	}
        	
        	return new LLBinaryOp(l, r, b.getOp(), b.getType());
        } else if (e instanceof LLVarLocation) {
        	return e;
        } else if (e instanceof LLUnaryNot) {
        	// Double not optimization
        	if (((LLUnaryNot)e).getExpr() instanceof LLUnaryNot) { 
        		LLUnaryNot inner = (LLUnaryNot)((LLUnaryNot)e).getExpr();
        		return reduceBooleanExpression(inner.getExpr());
        	}
        	
        	LLExpression expr = reduceBooleanExpression(((LLUnaryNot)e).getExpr());
        	LLVarLocation tmp = new LLVarLocation(1, getNextTemp());
        	LLAssign ass = new LLAssign(tmp, expr); 
        	
        	instructions.add(ass);
        	currentBlock.addInstruction(ass);
        	
        	LLUnaryNot n = new LLUnaryNot(tmp);
        	LLVarLocation tmp2 = new LLVarLocation(1, getNextTemp());
        	LLAssign ass2 = new LLAssign(tmp2, n); 
        	
        	instructions.add(ass2);
        	currentBlock.addInstruction(ass2);
        	
        	return tmp2;
        } else {
        	return e;
        }
    }
    
    
    
}
