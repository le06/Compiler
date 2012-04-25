package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.checker.Ir.IrBinOperator;

public class LLBinaryOp implements LLExpression {
    private LLExpression lhs;
    private LLExpression rhs;
    private IrBinOperator op;
    private Type type;
    
    String address_of_result;
    LLLabel short_circuit_label = null;
    
    public LLBinaryOp(LLExpression lhs, LLExpression rhs, IrBinOperator op,
    				  Type type) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = op;
        this.type = type;
        if (op == IrBinOperator.AND || op == IrBinOperator.OR) {
        	short_circuit_label = new LLLabel("short_circuit");
        }
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
	
	public void setLhs(LLExpression l) {
	    lhs = l;
	}

	public void setRhs(LLExpression r) {
        rhs = r;
    }
	
	public IrBinOperator getOp() {
		return op;
	}

	public LLLabel getLabel() {
		return short_circuit_label;
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

    @Override
    public String toString() {
        return lhs.toString() + " " + op.toString() + " " + rhs.toString();
    }
}
