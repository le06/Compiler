package edu.mit.compilers.checker.Ir;

public class IrLocalDecl extends Ir {
    public IrLocalDecl(IrIdentifier name) {
        id = name;
    }
    
	private IrIdentifier id;
	
	public IrIdentifier getId() {
		return id;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	public String toString() {
	    return id.toString();
	}
}
