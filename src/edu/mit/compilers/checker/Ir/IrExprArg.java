package edu.mit.compilers.checker.Ir;

public class IrExprArg extends IrCalloutArg {
    public IrExprArg(IrExpression argument) {
        arg = argument;
    }
    
	private IrExpression arg;
}