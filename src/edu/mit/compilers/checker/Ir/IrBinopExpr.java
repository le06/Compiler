package edu.mit.compilers.checker.Ir;

public class IrBinopExpr extends IrExpression {
    IrBinOperator operator;
    IrExpression lhs;
    IrExpression rhs;
}