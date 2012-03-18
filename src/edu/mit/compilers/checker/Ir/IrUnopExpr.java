package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llUnaryNeg;
import edu.mit.compilers.codegen.ll.llUnaryNot;

public class IrUnopExpr extends Ir implements IrExpression {
    public IrUnopExpr(IrUnaryOperator op, IrExpression expression) {
        operator = op;
        expr = expression;
    }
    
    public boolean isNegativeLiteral() {
    	if (operator == IrUnaryOperator.MINUS &&
    			(expr instanceof IrIntLiteral)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    private IrUnaryOperator operator;
    private IrExpression expr;

	public IrUnaryOperator getOperator() {
		return operator;
	}
	public IrExpression getExpr() {
		return expr;
	}
	
	@Override
	public IrType getExprType(IrNodeChecker c) {
		IrType exprType = expr.getExprType(c);
		
		if (operator == IrUnaryOperator.NOT &&
			exprType.myType == IrType.Type.BOOLEAN) {
			return new IrType(IrType.Type.BOOLEAN);
		} else if (operator == IrUnaryOperator.MINUS &&
				exprType.myType == IrType.Type.INT) {
			return new IrType(IrType.Type.INT);
		} else {
			return new IrType(IrType.Type.MIXED);
		}
	}
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	public String toString() {
	    return operator.toString() + " " + expr.toString();
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public llNode getllRep() {
        llExpression val = (llExpression)expr.getllRep();
        
        if (operator == IrUnaryOperator.MINUS) {
            return (llNode)(new llUnaryNeg(val));
        } else { // NOT
            return (llNode)(new llUnaryNot(val));
        }
    }
}