package edu.mit.compilers.codegen.ll;

public class LLUnaryNot implements LLExpression {
    private LLExpression negated_expr;
    private String address_of_result;
    
    public LLUnaryNot(LLExpression expr) {
        negated_expr = expr;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        negated_expr.accept(v);
        v.visit(this);
    }
    
    public LLExpression getExpr() {
    	return negated_expr;
    }
    
	@Override
	public String addressOfResult() {
		return address_of_result;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

    @Override
    public void setAddress(String addr) {
        address_of_result = addr;
    }
}
