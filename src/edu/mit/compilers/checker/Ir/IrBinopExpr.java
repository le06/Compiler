package edu.mit.compilers.checker.Ir;

public class IrBinopExpr extends Ir implements IrExpression {
    public IrBinopExpr(IrBinOperator op, IrExpression left, IrExpression right) {
        operator = op;
        lhs = left;
        rhs = right;
    }
    
    private IrBinOperator operator;
    private IrExpression lhs;
    private IrExpression rhs;
    
	@Override
	public IrType getExprType(IrNodeChecker c) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		lhs.accept(v);
		rhs.accept(v);
		v.visit(this);
	}
}