package edu.mit.compilers.codegen.ll;

public interface LLNodeVisitor {
    public void visit(LLFile node);
	public void visit(LLGlobalDecl node);
	public void visit(LLArrayDecl node);
	public void visit(LLMalloc node);
	
	public void visit(LLMethodDecl node);
	public void visit(LLEnvironment node);
	
	public void visit(LLAssign node);
	public void visit(LLMethodCall node);
	public void visit(LLCallout node);
	public void visit(LLStringLiteral node);
	
	public void visit(LLVarLocation node);
    public void visit(LLArrayLocation node);
    
    public void visit(LLBinaryOp node);
    public void visit(LLUnaryNeg node);
    public void visit(LLUnaryNot node);
    public void visit(LLBoolLiteral node);
    public void visit(LLIntLiteral node);
    
    public void visit(LLJump node);
    public void visit(LLLabel node);
    public void visit(LLMov node);
    public void visit(LLReturn node);
    
    public void visit(LLNop node);
}
