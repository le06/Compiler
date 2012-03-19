package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llNode;

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
	
	public String toString() {
	    return type.toString() + " " + id.toString();
	}
	
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public llNode getllRep() {
        return null;
    }
}
