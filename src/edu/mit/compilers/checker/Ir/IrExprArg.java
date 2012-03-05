package edu.mit.compilers.checker.Ir;

public class IrExprArg extends IrCalloutArg {
    public IrExprArg(IrExpression argument) {
        arg = argument;
    }
    
	private IrExpression arg;

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}