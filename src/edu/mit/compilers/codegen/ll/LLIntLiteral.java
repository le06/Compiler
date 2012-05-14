package edu.mit.compilers.codegen.ll;

public class LLIntLiteral implements LLExpression {
    private long value;
    String address_of_result;
    
    public LLIntLiteral(long val) {
        value = val;
        //address_of_result = address;
    }
    
    public long getValue() {
        return value;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		//return address_of_result;
		return "$" + value;
	}

	@Override
	public Type getType() {
		return Type.INT;
	}

    @Override
    public void setAddress(String addr) {
        address_of_result = addr;
    }
    
    @Override
    public String toString() {
        return Long.toString(value); 
    }
}
