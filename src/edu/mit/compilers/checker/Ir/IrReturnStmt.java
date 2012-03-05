package edu.mit.compilers.checker.Ir;

public class IrReturnStmt extends IrStatement {
    public IrReturnStmt(IrExpression expr) {
        return_expr = expr;
    }
    
	private IrExpression return_expr;
}