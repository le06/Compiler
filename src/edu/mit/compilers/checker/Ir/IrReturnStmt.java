package edu.mit.compilers.checker.Ir;

public class IrReturnStmt extends IrStatement {
    public IrReturnStmt(IrExpression expr) {
        return_expr = expr;
    }
    
	private IrExpression return_expr;

	public IrExpression getReturnExpr() {
		return return_expr;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}