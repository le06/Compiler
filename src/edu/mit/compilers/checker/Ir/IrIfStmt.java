package edu.mit.compilers.checker.Ir;

public class IrIfStmt extends IrStatement {
	private IrExpression condition;
	private IrBlock true_block;
	private IrBlock false_block;
}