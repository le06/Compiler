package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;

public class IrLocalDecl extends Ir {
    private IrIdentifier id;
    private String symbol;
    
    public IrLocalDecl(IrIdentifier name) {
        id = name;
    }
	
	public IrIdentifier getId() {
		return id;
	}
	
	public String getSymbol() {
	    return symbol;
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
        //return new LLVarDecl(id.getId());
        return new LLNop();
    }
}
