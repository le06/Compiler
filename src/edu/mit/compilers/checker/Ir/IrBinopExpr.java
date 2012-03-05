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
    
    public IrBinOperator getOperator() {
		return operator;
	}

	public IrExpression getLeft() {
		return lhs;
	}

	public IrExpression getRight() {
		return rhs;
	}

	public String toString() {
        return lhs.toString() + " " + operator.toString() + " " + rhs.toString();
    }

	@Override
	public IrType getExprType(IrNodeChecker c) {
		IrType lhs_type = lhs.getExprType(c);
		IrType rhs_type = rhs.getExprType(c);

		if (lhs_type.myType == rhs_type.myType) {
			return new IrType(lhs_type.myType);
		} else {
			return new IrType(IrType.Type.MIXED);
		}
		
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}