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
	public void accept(IrNodeVisitor v) {
		// do nothing! scanner enforces correctness.
	}

	@Override
	public IrType getExprType(IrNodeChecker c) {
		// identifiers != locations. an identifier type is meaningless.
		return null;
	}

	public String toString() {
	    return id;
	}
}