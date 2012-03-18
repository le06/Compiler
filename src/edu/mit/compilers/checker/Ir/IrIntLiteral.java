package edu.mit.compilers.checker.Ir;

import java.math.BigInteger;

public class IrIntLiteral extends Ir implements IrExpression {
    private String representation;
    private NumType num_type;
    private boolean is_negative;
    private long int_rep;
    
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
    
    public long getIntRep() {
        /*
         * private long parseIntLiteral(IrIntLiteral literal) throws
         * NumberFormatException {
         */

        IrIntLiteral.NumType num_type = this.getNumType();
        String representation = this.getRepresentation();

        BigInteger result;

        if (num_type == IrIntLiteral.NumType.DECIMAL) { // #####
            result = new BigInteger(representation);
        } else if (num_type == IrIntLiteral.NumType.HEX) { // 0x####
            result = new BigInteger(representation.substring(2), 16);
        } else { // 0b####
            result = new BigInteger(representation.substring(2), 2);
        }

        if (this.isNegative()) {
            result = result.negate();
        }

        Long truncated_result = result.longValue();
        BigInteger comparison = new BigInteger(truncated_result.toString());

        if (!result.equals(comparison)) {
            throw new NumberFormatException();
        }

        return truncated_result;
    }
    
    
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