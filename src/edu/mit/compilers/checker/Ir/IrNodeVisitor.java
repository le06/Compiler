package edu.mit.compilers.checker.Ir;

public interface IrNodeVisitor {
	public void visit(IrClassDecl node);
	
	public void visit(IrFieldDecl node);
	
	public void visit(IrBaseDecl node);

	public void visit(IrArrayDecl node);

	public void visit(IrMethodDecl node);
	
	public void visit(IrVarDecl node);
	
	public void visit(IrLocalDecl node);
	
	public void visit(IrBlockStmt node);
	
	public void visit(IrContinueStmt node);

	public void visit(IrBreakStmt node);
	
	public void visit(IrReturnStmt node);
	
	public void visit(IrWhileStmt node);

	public void visit(IrForStmt node);
	
	public void visit(IrIfStmt node);
	
	public void visit(IrMethodCallStmt node);

	public void visit(IrCalloutStmt node);	
	
	public void visit(IrAssignStmt node);

	public void visit(IrPlusAssignStmt node);

	public void visit(IrMinusAssignStmt node);

	public void visit(IrVarLocation node);

	public void visit(IrArrayLocation node);

	public void visit(IrBinopExpr node);

	public void visit(IrUnopExpr node);

	public void visit(IrIntLiteral node);
}