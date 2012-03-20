package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrContinueStmt extends IrStatement {

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	public String toString() {
	    return "continue";
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
        return null;
    }
}