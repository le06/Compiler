package edu.mit.compilers.checker.Ir;

public class IrUnopExpr extends Ir implements IrExpression {
    public IrUnopExpr(IrUnaryOperator op, IrExpression expression) {
        operator = op;
        expr = expression;
    }
    
    private IrUnaryOperator operator;
    private IrExpression expr;

	@Override
	public IrType getExprType(IrNodeChecker c) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}