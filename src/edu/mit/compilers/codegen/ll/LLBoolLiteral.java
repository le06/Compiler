package edu.mit.compilers.codegen.ll;

public class LLBoolLiteral implements LLExpression {
    private boolean val;
    String address_of_result;
    
    public LLBoolLiteral(boolean value, String address) {
        val = value;
        address_of_result = address;
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
		return address_of_result;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

}
