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
        
        if (node.getExpr().getType() == Type.INT) {
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LLBoolLiteral node) {
        // Should not have to do anything
    }

    @Override
    public void visit(LLIntLiteral node) {
        // Should not have to do anything
    }

    private void writeCmp(LLBinaryOp op, LLLabel target) {
    	LLCmp cmp;
    	LLJump jmp;
    	
    	switch (op.getOp()) {
    	case EQ:
            cmp = new LLCmp(op.getRhs(), op.getLhs());
            jmp = new LLJump(JumpType.EQUAL, target);
            break;
        case NEQ:
        	cmp = new LLCmp(op.getRhs(), op.getLhs());
            jmp = new LLJump(JumpType.NOT_EQUAL, target);
            break;
        case GEQ:
        	cmp = new LLCmp(op.getRhs(), op.getLhs());
        	jmp = new LLJump(JumpType.GEQ, target);
            break;
        case GT:
	        cmp = new LLCmp(op.getRhs(), op.getLhs());
	        jmp = new LLJump(JumpType.GT, target);
            break;
        case LEQ:
        	cmp = new LLCmp(op.getRhs(), op.getLhs());
            jmp = new LLJump(JumpType.LEQ, target);
            break;
        case LT:
        	cmp = new LLCmp(op.getRhs(), op.getLhs());
            jmp = new LLJump(JumpType.LT, target);
            break;
        default:
        	throw new RuntimeException("Shouldn't be here");
    	}
    	
        instructions.add(cmp);
        currentBlock.addInstruction(cmp);
        instructions.add(jmp);
        currentBlock.addInstruction(jmp);
    }
    
    private void reduceBooleanExpression(LLExpression cond, LLLabel jmpPoint) {
        if (cond instanceof LLBinaryOp) {
        	LLBinaryOp op = (LLBinaryOp)cond;
        	switch (op.getOp()) {
        	case EQ:
            case NEQ:
            case GEQ:
            case GT:
            case LEQ:
            case LT:
            	writeCmp(op, jmpPoint);
                break;
            case AND:
            	
            	break;
                
            case OR:
            	
            	break;
        	}
        	
        } else if (cond instanceof LLVarLocation) {
        	LLCmp cmp = new LLCmp((LLVarLocation)cond);
        	LLJump jmp = new LLJump(JumpType.NOT_EQUAL, jmpPoint);
        	
        	instructions.add(cmp);
            currentBlock.addInstruction(cmp);
            instructions.add(jmp);
            currentBlock.addInstruction(jmp);
        } 
    }
    
    @Override
    public void visit(LLJump node) {
    	if (node.getCond() == null) {
    		instructions.add(node);
            currentBlock.addInstruction(node);
    	} else {
    		reduceBooleanExpression(node.getCond(), node.getLabel());
    	}
    
        
        BasicBlock newBlock = new BasicBlock();
        // At a jump, the current basic block splits to have two children:
        // taking the jump and not taking the jump
        currentBlock.addChild(newBlock);

        // As the label may not be in the table yet, cache this relationship
        // and apply it at the end
        blockCache.add(currentBlock);
        targetCache.add(node.getLabel().getName());
        
        // Following instructions are in own basic block
        currentBlock = newBlock; 
        blocksInOrder.add(currentBlock);
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
        } else if (e instanceof LLUnaryNeg) {
            return null;
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
        	return e; // TODO
        } else if (e instanceof LLVarLocation) {
        	return e;
        } else {
        	return e;
        }
    }
    
    
    
}

	
    


 /*   private LLExpression reduceBooleanExpression(LLExpression e, LLLabel exitLabel, boolean invert) {
        if (e instanceof LLBinaryOp) {
            return reduceBooleanBinOp((LLBinaryOp)e, exitLabel, invert);
            // TODO: Literals, unary not
        } else if (e instanceof LLBoolLiteral) {
            return e;
        }else {
            return e;
        }
    }
    
    private void reduceAnd(LLBinaryOp e) {
        
    }
    
    private void reduceOr(LLBinaryOp e) {
        
    }
    
    private void reduceCmp(LLBinaryOp e, LLLabel exitLabel, boolean invert) {
        switch (e.getOp()) {
        case EQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.NOT_EQUAL, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.EQUAL, exitLabel));
            }
            break;
            
        case NEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.EQUAL, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.NOT_EQUAL, exitLabel));
            }
            break;
        case GEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.LT, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.GEQ, exitLabel));
            }
            break;
        case GT:
            if (invert) {
                instructions.add(new LLJump(JumpType.LEQ, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.GT, exitLabel));
            }
            break;
        case LEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.GT, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.LEQ, exitLabel));
            }
            break;
        case LT:
            if (invert) {
                instructions.add(new LLJump(JumpType.GEQ, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.LT, exitLabel));
            }
            break;
        default:
            throw new RuntimeException("Non bool type in bool expression");
        }
    }
    
    private LLExpression reduceBooleanBinOp(LLBinaryOp e, LLLabel exitLabel, boolean invert) {
        LLLabel ssLabel = e.getLabel();
        
        if (e.getOp() == IrBinOperator.OR) {
         // TODO: short circuit logic
            
            
        } else if (e.getOp() == IrBinOperator.AND) {
            LLExpression l = reduceBooleanExpression(e.getLhs(), ssLabel, true);
            LLBinaryOp ss_op = new LLBinaryOp(l, new LLIntLiteral(0), IrBinOperator.EQ, Type.BOOLEAN);
            LLJump newJmp = new LLJump(JumpType.EQUAL, ssLabel);
            
            
        } else {
            return e;
        }
        
        
        switch (e.getOp()) {
        case OR:
            
            break;
            
        case AND:
            
            break;
            
        case EQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.NOT_EQUAL, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.EQUAL, exitLabel));
            }
            break;
            
        case NEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.EQUAL, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.NOT_EQUAL, exitLabel));
            }
            break;
        case GEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.LT, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.GEQ, exitLabel));
            }
            break;
        case GT:
            if (invert) {
                instructions.add(new LLJump(JumpType.LEQ, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.GT, exitLabel));
            }
            break;
        case LEQ:
            if (invert) {
                instructions.add(new LLJump(JumpType.GT, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.LEQ, exitLabel));
            }
            break;
        case LT:
            if (invert) {
                instructions.add(new LLJump(JumpType.GEQ, exitLabel));
            } else {
                instructions.add(new LLJump(JumpType.LT, exitLabel));
            }
            break;
        default:
            throw new RuntimeException("Non bool type in bool expression");
        }
        
        return null;
    }*/

   /* @Override
    public void visit(LLCmp node) {
        // Do nothing at this stage
    }
    
    
/*    
     * ASM run-time error methods.
     
    private void error_array_oob(LLFile node) {
        LLLabel l = new LLLabel("ARRAY_OUT_OF_BOUNDS");
        //array_oob_label.accept(this);
        
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
}*/
