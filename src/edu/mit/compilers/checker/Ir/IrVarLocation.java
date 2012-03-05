package edu.mit.compilers.checker.Ir;

public class IrVarLocation extends IrLocation {
    public IrVarLocation(IrIdentifier var) {
        id = var;
    }
    
	private IrIdentifier id;
}