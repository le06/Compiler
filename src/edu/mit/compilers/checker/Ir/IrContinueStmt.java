package edu.mit.compilers.checker.Ir;

public class IrContinueStmt extends IrStatement {

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}