package edu.mit.compilers.checker.Ir;

public class IrArrayLocation extends IrLocation {
    public IrArrayLocation(IrIdentifier name, IrExpression expr) {
        id = name;
        index = expr;
    }
    
	private IrIdentifier id;
	private IrExpression index;
}