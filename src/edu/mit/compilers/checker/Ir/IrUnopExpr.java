package edu.mit.compilers.checker.Ir;

public class IrUnopExpr extends IrExpression {
    public IrUnopExpr(IrUnaryOperator op, IrExpression expression) {
        operator = op;
        expr = expression;
    }
    
    private IrUnaryOperator operator;
    private IrExpression expr;
}