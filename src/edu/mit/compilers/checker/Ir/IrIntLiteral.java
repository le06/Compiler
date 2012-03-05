package edu.mit.compilers.checker.Ir;

public class IrIntLiteral extends Ir implements IrExpression {
    public IrIntLiteral(String value, NumType type) {
        representation = value;
        num_type = type;
        is_negative = false;
    }
    
    public IrIntLiteral(String value, NumType type, boolean is_negative) {
        representation = value;
        num_type = type;
        this.is_negative = is_negative;
    }
    
    private String representation;
    private NumType num_type;
    private boolean is_negative;
    
    public String getRepresentation() {
		return representation;
	}

	public NumType getNumType() {
		return num_type;
	}

	public boolean isNegative() {
		return is_negative;
	}
	
	public enum NumType {
        DECIMAL,
        HEX,
        BINARY;
    }

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

	@Override
	public IrType getExprType(IrNodeChecker c) {
		return new IrType(IrType.Type.INT);
	}
	
	public String toString() {
	    return representation;
	}
	
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }
}