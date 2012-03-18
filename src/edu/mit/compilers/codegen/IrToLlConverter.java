package edu.mit.compilers.codegen;

import edu.mit.compilers.checker.Ir.*;
import edu.mit.compilers.codegen.ll.*;

public class IrToLlConverter implements IrNodeVisitor<llNode> {
    llFile output;
    llMethodDef currentMethod;
    llEnvironment currentEnvironment;
    
    public IrToLlConverter() {
        output = new llFile();
    }
    
    public llNode subExprConvert(llNode node) {
        return null;
    }

    @Override
    public llNode visit(IrClassDecl node) {
        // Don't need to do anything -- visitor will visit 
        // children recursively
    }

    @Override
    public llNode visit(IrFieldDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrBaseDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrArrayDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrMethodDecl node) {
        currentEnvironment = new llEnvironment();
        llMethodDef m = new llMethodDef(node.getId().getId(), currentEnvironment);
        
        if (node.getId().getId().equals("main")) {
            output.setMain(m);
        } else {
            output.addMethod(m);
        }
    }

    @Override
    public llNode visit(IrVarDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrLocalDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrBlockStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrContinueStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrBreakStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrReturnStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrWhileStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrForStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrIfStmt node) {
        //llEnvironment e = new llEnvironment();
        
        llLabel true_label = new llLabel();
        llLabel if_end = new llLabel();
        
        llJump jump_true = new llJump(llJump.JumpType.NOT_EQUAL, true_label);
        llJump end_true = new llJump(llJump.JumpType.UNCONDITIONAL, if_end);
        llJump end_false = new llJump(llJump.JumpType.UNCONDITIONAL, if_end);
        
        llEnvironment eval_cond_block = new llEnvironment();
        llEnvironment true_block = new llEnvironment();
        llEnvironment false_block = new llEnvironment();
        
        currentEnvironment.addNode(eval_cond_block);
        currentEnvironment.addNode(jump_true);
        currentEnvironment.addNode(false_block);
        currentEnvironment.addNode(end_false);
        currentEnvironment.addNode(true_label);
        currentEnvironment.addNode(node)
        
        //currentEnvironment.addNode(e);
        
    }

    @Override
    public llNode visit(IrMethodCallStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrCalloutStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrPlusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrMinusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrVarLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrArrayLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrBinopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrUnopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public llNode visit(IrIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

}
