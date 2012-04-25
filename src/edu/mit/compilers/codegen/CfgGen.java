package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.ranges.RangeException;

import edu.mit.compilers.codegen.ll.*;

public class CfgGen implements LLNodeVisitor {
    private BasicBlock currentBlock, head;
    private ArrayList<LLNode> instructions;
    private String TEMP = "t";
    private int currentTemp = 0;
    
    private HashMap<String, BasicBlock> blockTable;
    
    public void generateCFG(LLFile file) {
        instructions = new ArrayList<LLNode>();
        blockTable = new HashMap<String, BasicBlock>();
        
        currentBlock = new BasicBlock();
        head = currentBlock;
        
        file.accept(this);
    }
    
    public BasicBlock getCFG() {
        return head;
    }
    
    public ArrayList<LLNode> getInstructions() {
        return instructions;
    }

    private String getNextTemp() {
        return TEMP + currentTemp++;
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
        }

        //mainDirective(); // write ".globl main".
        node.getMain().accept(this);
        
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
    }

    @Override
    public void visit(LLArrayDecl node) {
        instructions.add(node);
    }

    @Override
    public void visit(LLMalloc node) {
        instructions.add(node);
    }

    @Override
    public void visit(LLMethodDecl node) {
        currentBlock = new BasicBlock();
        head.addChild(currentBlock);
        
        currentBlock.addInstruction(node);
        instructions.add(node);
        
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
        LLAssign newAss = new LLAssign(node.getLoc(), 
                                       reduceExpression(node.getExpr()));
        
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
        // TODO Deal with arguments here
        instructions.add(node);
        currentBlock.addInstruction(node);
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
        
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLBinaryOp node) {
        // Do nothing - handled in upper level expressions
        //reduceBinOp(node);
    }
    
    private LLExpression reduceExpression(LLExpression e) {
        //LLVarLocation outLoc;
        
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
        
        //return outLoc;
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
        LLVarLocation out = new LLVarLocation(1, getNextTemp());
        
        LLAssign ass = new LLAssign(out, m);
        
        // Add this instruction to the output
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

    @Override
    public void visit(LLJump node) {
        currentBlock.addInstruction(node);
        instructions.add(node);
        
        BasicBlock newBlock = new BasicBlock();
        // At a jump, the current basic block splits to have two children:
        // taking the jump and not taking the jump
        currentBlock.addChild(newBlock);
        currentBlock.addChild(blockTable.get(node.getLabel().getName())); // TODO label may occur later in file, cache for later
        // Following instructions are in own basic block
        currentBlock = newBlock; 
    }

    @Override
    public void visit(LLLabel node) {
        BasicBlock newBlock = new BasicBlock();
        newBlock.addInstruction(node);
        instructions.add(node);
        
        blockTable.put(node.getName(), newBlock);
        
        currentBlock.addChild(newBlock);
        currentBlock = newBlock;
    }

    @Override
    public void visit(LLMov node) {
        instructions.add(node);
        currentBlock.addInstruction(node);
    }

    @Override
    public void visit(LLReturn node) {
        if (node.getExpr() == null) {
            LLReturn newRet = new LLReturn(reduceExpression(node.getExpr()));
            currentBlock.addInstruction(newRet);
            instructions.add(newRet);
        } else {
            currentBlock.addInstruction(node);
            instructions.add(node);
        }
    }

    @Override
    public void visit(LLNop node) {
        // Do nothing
    }

}
