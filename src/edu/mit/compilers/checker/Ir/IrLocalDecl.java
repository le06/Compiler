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
	
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
}
