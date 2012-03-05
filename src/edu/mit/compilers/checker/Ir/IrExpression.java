package edu.mit.compilers.checker.Ir;

public interface IrExpression extends IrNode {
	// note the argument: type lookup is based on context.
	// specifically, this is necessary to determine the type of a location.
    IrType getExprType(IrNodeChecker v);
    
    boolean isNot();
    boolean isNeg();
    void setNot();
    void setNeg();

    String toString(int i);
}