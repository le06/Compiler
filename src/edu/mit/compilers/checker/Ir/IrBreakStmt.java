package edu.mit.compilers.checker.Ir;

public class IrBreakStmt extends IrStatement {

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}