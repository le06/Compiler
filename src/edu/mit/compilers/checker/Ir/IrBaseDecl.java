package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLGlobalDecl;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrBaseDecl extends IrGlobalDecl {
    private IrIdentifier id;
    private String symbol;
    
    public IrBaseDecl(IrIdentifier name) {
        id =  name;
    }
	
	public IrIdentifier getId() {
		return id;
	}
	
	public void setSymbol(String symbol) {
	    this.symbol = symbol;
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLLabel g = new LLLabel(id.getId());
        return (LLNode)(new LLGlobalDecl(g));
    }
}