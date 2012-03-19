package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llStringLiteral;

public class IrStringLiteral extends Ir {
    public IrStringLiteral(String text) {
        literal = text;
    }
    
	private String literal;

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// no need to visit; correctness enforced by scanner.
	}
	
	public String toString() {
	    return literal;
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