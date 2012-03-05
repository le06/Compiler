package edu.mit.compilers.checker.Ir;

public class IrParameterDecl extends Ir {
	private IrType type;
	private IrIdentifier id;
	
	public IrParameterDecl(IrType t, IrIdentifier name) {
	    type = t;
	    id = name;
	}
	
	public IrType getType() {
		return type;
	}
	public IrIdentifier getId() {
		return id;
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// do nothing! v never accepts this class.
	}
}
