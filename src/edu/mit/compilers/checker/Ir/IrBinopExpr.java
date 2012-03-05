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
    
    public String toString() {
        return lhs.toString() + " " + operator.toString() + " " + rhs.toString();
    }
}