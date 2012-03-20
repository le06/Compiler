package edu.mit.compilers.codegen;

import edu.mit.compilers.checker.Ir.*;
import edu.mit.compilers.codegen.ll.*;

public class IrToLlConverter implements IrNodeVisitor<LLNode> {
    LLFile output;
    LLMethodDecl currentMethod;
    LLEnvironment currentEnvironment;
    
    public IrToLlConverter() {
        output = new LLFile();
    }
    
    public LLNode subExprConvert(LLNode node) {
        return null;
    }

    @Override
    public LLNode visit(IrClassDecl node) {
        // Don't need to do anything -- visitor will visit 
        // children recursively
    }

    @Override
    public LLNode visit(IrFieldDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrBaseDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrArrayDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrMethodDecl node) {
        currentEnvironment = new LLEnvironment();
        LLMethodDecl m = new LLMethodDecl(node.getId().getId(), currentEnvironment);
        
        if (node.getId().getId().equals("main")) {
            output.setMain(m);
        } else {
            output.addMethod(m);
        }
    }

    @Override
    public LLNode visit(IrVarDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrLocalDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrBlockStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrContinueStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrBreakStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrReturnStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrWhileStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrForStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrIfStmt node) {
        //llEnvironment e = new llEnvironment();
        
        LLLabel true_label = new LLLabel();
        LLLabel if_end = new LLLabel();
        
        LLJump jump_true = new LLJump(LLJump.JumpType.NOT_EQUAL, true_label);
        LLJump end_true = new LLJump(LLJump.JumpType.UNCONDITIONAL, if_end);
        LLJump end_false = new LLJump(LLJump.JumpType.UNCONDITIONAL, if_end);
        
        LLEnvironment eval_cond_block = new LLEnvironment();
        LLEnvironment true_block = new LLEnvironment();
        LLEnvironment false_block = new LLEnvironment();
        
        currentEnvironment.addNode(eval_cond_block);
        currentEnvironment.addNode(jump_true);
        currentEnvironment.addNode(false_block);
        currentEnvironment.addNode(end_false);
        currentEnvironment.addNode(true_label);
        currentEnvironment.addNode(node);
        
        //currentEnvironment.addNode(e);
        
    }

    @Override
    public LLNode visit(IrMethodCallStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrCalloutStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrPlusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrMinusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrVarLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrArrayLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrBinopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrUnopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LLNode visit(IrIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

}
