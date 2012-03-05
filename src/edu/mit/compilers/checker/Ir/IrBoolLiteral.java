package edu.mit.compilers.checker.Ir;

public class IrBoolLiteral extends Ir implements IrExpression {
    public IrBoolLiteral(boolean val) {
        literal = val;
    }
    
    private boolean literal;

	@Override
	public IrType getExprType(IrNodeChecker c) {
		return new IrType(IrType.Type.BOOLEAN);
	}

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// no need to visit; the parser guarantees validity.
	}
	
	public String toString() {
	    return Boolean.toString(literal);
	}
}