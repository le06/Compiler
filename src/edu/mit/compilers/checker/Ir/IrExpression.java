package edu.mit.compilers.checker.Ir;

public interface IrExpression {
    IrType getType();
    long getIntVal();
    boolean getBoolVal();
}