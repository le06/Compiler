package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llVarDec;

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
    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        return new llVarDec(id.getId());
    }
}
