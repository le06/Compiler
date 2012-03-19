package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llNode;

public class IrStringArg extends IrCalloutArg {
    public IrStringArg(IrStringLiteral argument) {
        arg = argument;
    }
    
	private IrStringLiteral arg;

	@Override
	public void accept(IrNodeVisitor v) {
		// no need to visit. scanner enforces correctness
	}
	
	public String toString() {
	    return arg.toString();
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
        // TODO Auto-generated method stub
        return null;
    }
}