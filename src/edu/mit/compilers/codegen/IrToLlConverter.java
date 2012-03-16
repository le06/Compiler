package edu.mit.compilers.codegen;

import edu.mit.compilers.checker.Ir.*;
import edu.mit.compilers.codegen.ll.*;

public class IrToLlConverter implements IrNodeVisitor {
    llFile output;
    
    public IrToLlConverter() {
        output = new llFile();
    }

    @Override
    public void visit(IrClassDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrFieldDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrBaseDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrArrayDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrMethodDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrVarDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrLocalDecl node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrBlockStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrContinueStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrBreakStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrReturnStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrWhileStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrForStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrIfStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrMethodCallStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrCalloutStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrPlusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrMinusAssignStmt node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrVarLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrArrayLocation node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrBinopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrUnopExpr node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IrIntLiteral node) {
        // TODO Auto-generated method stub
        
    }

}
