package edu.mit.compilers.checker.Ir;

public class IrForStmt extends IrStatement {
	private IrIdentifier counter;
	private IrExpression start_value;
	private IrExpression stop_value;
	private IrBlock block;
}