package edu.mit.compilers.codegen.ll;

public interface llNodeVisitor {
    public void visit(llArrayAccess node);
    
    public void visit(llArrayDec node);
    
    public void visit(llAssign node);
    
    public void visit(llBinOp node);
    
    public void visit(llBoolLiteral node);
    
    public void visit(llCallout node);
    
    public void visit(llEnvironment node);
    
    public void visit(llExpression node);
    
    public void visit(llFile node);
    
    public void visit(llFunctionCall node);
    
    public void visit(llGlobalDec node);
    
    public void visit(llIntLiteral node);
    
    public void visit(llJump node);
    
    public void visit(llLabel node);
    
    public void visit(llMethodCall node);
    
    public void visit(llNode node);
    
    public void visit(llPop node);
    
    public void visit(llPush node);
    
    public void visit(llReturn node);
    
    public void visit(llStringLiteral node);
    
    public void visit(llUnaryNeg node);
    
    public void visit(llUnaryNot node);
    
    public void visit(llVarAccess node);
    
    public void visit(llVarDec node);
}
