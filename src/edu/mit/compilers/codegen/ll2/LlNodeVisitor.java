package edu.mit.compilers.codegen.ll2;

public interface LlNodeVisitor {

    public void visit(LlAnnotation node);
    public void visit(LlArrayDecl node);
    public void visit(LlArrayLoc node);
    public void visit(LlAssign node);
    public void visit(LlBinaryAssign node);
    public void visit(LlBlock node);
    public void visit(LlBoolLiteral node);
    public void visit(LlCallout node);
    public void visit(LlCmp node);
    public void visit(LlGlobalDecl node);
    public void visit(LlGlobalLoc node);
    public void visit(LlIntLiteral node);
    public void visit(LlJmp node);
    public void visit(LlLabel node);
    public void visit(LlMethodCall node);
    public void visit(LlMethodDecl node);
    public void visit(LlProgram node);
    public void visit(LlRegLoc node);
    public void visit(LlReturn node);
    public void visit(LlStringLiteral node);
    public void visit(LlTempLoc node);
}