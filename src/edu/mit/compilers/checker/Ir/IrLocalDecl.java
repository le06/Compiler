package edu.mit.compilers.checker.Ir;

public class IrLocalDecl extends Ir {
	private IrIdentifier id;
	
	public IrIdentifier getId() {
		return id;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}
