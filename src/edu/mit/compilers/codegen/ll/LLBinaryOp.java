package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.checker.Ir.IrBinOperator;

public class LLBinaryOp implements LLExpression {
    private LLExpression lhs;
    private LLExpression rhs;
    private IrBinOperator op;
    private Type type;
    
    String address_of_result;
    
    public LLBinaryOp(LLExpression lhs, LLExpression rhs, IrBinOperator op,
    				  Type type) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
        this.type = type;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
	public LLExpression getLhs() {
		return lhs;
	}

	public LLExpression getRhs() {
		return rhs;
	}

	public IrBinOperator getOp() {
		return op;
	}

	@Override
	public String addressOfResult() {
		return address_of_result;
	}

	@Override
	public Type getType() {
		return type;
	}

    @Override
    public void setAddress(String addr) {
        address_of_result = addr;
    }

}
