package edu.mit.compilers.checker.Ir;

public class IrArrayLocation extends IrLocation {
    public IrArrayLocation(IrIdentifier name, IrExpression expr) {
        id = name;
        index = expr;
    }
    
	private IrIdentifier id;
	private IrExpression index;
	

	public String toString() {
	    return id.toString() + "[\n" + index.toString() + "\n]";
	}

	public IrIdentifier getId() {
		return id;
	}
	public IrExpression getIndex() {
		return index;
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		return c.lookupArrayType(id);
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
		index.accept(v);
	}
}