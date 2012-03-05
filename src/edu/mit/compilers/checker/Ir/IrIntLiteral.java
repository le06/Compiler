package edu.mit.compilers.checker.Ir;

public class IrIntLiteral extends Ir implements IrExpression {
    public IrIntLiteral(String value, NumType type) {
        representation = value;
        num_type = type;
    }
    
    private String representation;
    private NumType num_type;
    
    public String getRepresentation() {
		return representation;
	}

	public NumType getNumType() {
		return num_type;
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