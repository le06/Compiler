package edu.mit.compilers.checker.Ir;

public class IrStringArg extends IrCalloutArg {
    public IrStringArg(IrStringLiteral argument) {
        arg = argument;
    }
    
	private IrStringLiteral arg;

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}