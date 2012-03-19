package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llNode;

public class IrExprArg extends IrCalloutArg {
    public IrExprArg(IrExpression argument) {
        arg = argument;
    }
    
	private IrExpression arg;

	@Override
	public void accept(IrNodeVisitor v) {
		arg.accept(v); // is the arg well-formed?
	}
	
	public String toString() {
	    return arg.toString();
	}
	
	public String toString(int s) {
        return arg.toString(s);
    }

    @Override
    public llNode getllRep() {
        return (llNode)((llExpression)arg.getllRep());
    }
}