package edu.mit.compilers.checker.Ir;

public class IrVarLocation extends IrLocation {
    public IrVarLocation(IrIdentifier var) {
        id = var;
    }
    
	private IrIdentifier id;

	public IrIdentifier getId() {
		return id;
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupVarType(id);
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}