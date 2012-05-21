package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrExprArg extends IrCalloutArg {
    public IrExprArg(IrExpression argument) {
        arg = argument;
    }
    
	private IrExpression arg;

	public IrExpression getArg() {
	    return arg;
	}
	
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return (LLNode)((LLExpression)arg.getllRep(null, null));
    }
}