package edu.mit.compilers.checker.Ir;

public class IrBaseDecl extends IrGlobalDecl {
	private IrIdentifier id;
	
	public IrIdentifier getId() {
		return id;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}