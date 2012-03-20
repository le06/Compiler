package edu.mit.compilers.codegen.ll;

public class LLUnaryNot implements LLExpression {
    LLExpression negated_expr;
    String address_of_result;
    
    public LLUnaryNot(LLExpression expr, String address) {
        negated_expr = expr;
        address_of_result = address;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        negated_expr.accept(v);
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
	
	/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("imul $-1, ");
    }*/
}
