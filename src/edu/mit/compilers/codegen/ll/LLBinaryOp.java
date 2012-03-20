package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.checker.Ir.IrBinOperator;

public class LLBinaryOp implements LLExpression {
    LLExpression lhs;
    LLExpression rhs;
    IrBinOperator op;
    Type type;
    
    String address_of_result;
    
    public LLBinaryOp(LLExpression lhs, LLExpression rhs, IrBinOperator op,
    				  Type type, String address) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
        this.type = type;
        address_of_result = address;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        lhs.accept(v);
        rhs.accept(v);
        v.visit(this);
    }
    
	@Override
	public String addressOfResult() {
		return address_of_result;
	}

	@Override
	public Type getType() {
		return type;
	}

}
