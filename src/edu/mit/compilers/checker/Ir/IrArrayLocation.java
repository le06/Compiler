package edu.mit.compilers.checker.Ir;

public class IrArrayLocation extends IrLocation {
    public IrArrayLocation(IrIdentifier name, IrExpression expr) {
        id = name;
        index = expr;
    }
    
	private IrIdentifier id;
	private IrExpression index;
	private boolean not = false, neg = false;

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
	
	public String toString() {
        return id.toString() + "[" + index.toString() + "]";
    }
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
    @Override
    public boolean isNot() {
        return not;
    }
    @Override
    public boolean isNeg() {
        return neg;
    }
    @Override
    public void setNot() {
        not = true;
        
    }
    @Override
    public void setNeg() {
        neg = true;
    }
}