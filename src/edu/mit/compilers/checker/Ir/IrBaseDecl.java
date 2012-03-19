package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llGlobalDec;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public class IrBaseDecl extends IrGlobalDecl {
    public IrBaseDecl(IrIdentifier name) {
        id =  name;
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
	
	@Override
    public String toString(int spaces_before) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < spaces_before; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        return (llNode)(new llGlobalDec(id.getId()));
    }
}