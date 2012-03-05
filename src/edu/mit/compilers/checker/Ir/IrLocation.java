package edu.mit.compilers.checker.Ir;

public abstract class IrLocation extends Ir implements IrExpression {
	public abstract IrIdentifier getId();
}