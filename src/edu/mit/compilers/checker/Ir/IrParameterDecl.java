package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;

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
	    v.visit(this);
		// TODO Auto-generated method stub
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return new LLNop();
    }
}
