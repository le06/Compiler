public interface IrNodeVisitor {
	public void visit(IrClassDecl node);

	public void visit(IrGlobalDecl node);

	public void visit(IrArrayDecl node);

	public void visit(IrMethodDecl node);

	public void visit(IrVarDecl node);

	public void visit(IrBlock node);

	public void visit(IrAssignStmt node);

	public void visit(IrPlusAssignStmt node);

	public void visit(IrMinusAssignStmt node);

	public void visit(IrContinueStmt node);

	public void visit(IrBreakStmt node);

	public void visit(IrReturnStmt node);

	public void visit(IrWhileStmt node);

	public void visit(IrForStmt node);

	public void visit(IrIfStmt node);

	public void visit(IrMethodCallStmt node);

	public void visit(IrCalloutStmt node);

	public void visit(IrStringArg node);

	public void visit(IrExprArg node);

	public void visit(IrVarLocation node);

	public void visit(IrArrayLocation node);

	public void visit(IrLocationExpr node);

	public void visit(IrMethodCallExpr node);

	public void visit(IrCalloutExpr node);

	public void visit(IrLiteralExpr node);

	public void visit(IrBinopExpr node);

	public void visit(IrUnopExpr node);

	public void visit(IrBinOperator node);

	public void visit(IrUnaryOperator node);

	public void visit(IrType node);

	public void visit(IrReturnType node);

	public void visit(IrIdentifier node);

	public void visit(IrIntLiteral node);

	public void visit(IrCharLiteral node);

	public void visit(IrBoolLiteral node);

	public void visit(IrStringLiteral node);
}