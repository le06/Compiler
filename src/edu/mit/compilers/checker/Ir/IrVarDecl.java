package edu.mit.compilers.checker.Ir;

public class IrVarDecl extends Ir {
	private IrType type;
	private IrIdentifier id;
	
	public IrType getType() {
		return type;
	}
	public IrIdentifier getId() {
		return id;
	}
}