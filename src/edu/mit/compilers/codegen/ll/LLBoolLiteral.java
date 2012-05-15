package edu.mit.compilers.codegen.ll;

public class LLBoolLiteral implements LLExpression {
    private boolean val;
    String address_of_result;
    
    public LLBoolLiteral(boolean value) {
        val = value;
        //address_of_result = address;
    }
    
    public boolean getValue() {
        return val;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		if (val) {
			return "$1";
		} else {
			return "$0";
		}
		
		//return address_of_result;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

    @Override
    public void setAddress(String addr) {
        address_of_result = addr;
    }
    
    @Override
    public String toString() {
        return Boolean.toString(val);
    }
}
