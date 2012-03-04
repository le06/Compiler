package edu.mit.compilers.checker.Ir;

public class IrUnopExpr extends IrExpression {
    IrUnaryOperator operator;
    IrExpression expr;
}