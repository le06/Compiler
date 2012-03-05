package edu.mit.compilers.checker.Ir;

public class IrGlobalDecl extends IrFieldDecl {
	private IrType type;
	private IrIdentifier id;
	
	public IrType getType() {
		return type;
	}
	public IrIdentifier getId() {
		return id;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}