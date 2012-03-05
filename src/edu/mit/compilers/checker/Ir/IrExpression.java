package edu.mit.compilers.checker.Ir;

public interface IrExpression {
    IrType type = new IrType(IrType.Type.VOID);
    long int_val = 0;
    boolean bool_val = false;
}