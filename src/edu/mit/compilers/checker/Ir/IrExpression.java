package edu.mit.compilers.checker.Ir;

public interface IrExpression extends IrNode {
	// note the argument: type lookup is based on context.
	// specifically, this is necessary to determine the type of a location.
    IrType getExprType(IrNodeChecker v);

    String toString(int i);
}