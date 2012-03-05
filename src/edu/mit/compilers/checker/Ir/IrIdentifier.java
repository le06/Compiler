package edu.mit.compilers.checker.Ir;

public class IrIdentifier extends Ir implements IrExpression {
    public IrIdentifier(String name) {
        id = name;
    }
    
    private String id;

	public String getId() {
		return id;
	}
    
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupVarType(this);
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		// do nothing! scanner enforces correctness.
	}

	public String toString() {
	    return id;
	}
}