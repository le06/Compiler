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
	
	public String toString() {
	    return id.toString();
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
}