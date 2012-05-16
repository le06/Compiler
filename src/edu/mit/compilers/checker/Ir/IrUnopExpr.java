package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.SemanticChecker;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLUnaryNot;

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
	public IrType getExprType(SemanticChecker c) {
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLExpression val = (LLExpression)expr.getllRep(null, null);
        
        if (operator == IrUnaryOperator.MINUS) {
            return (LLNode)(new LLUnaryNeg(val));
        } else { // NOT
            return (LLNode)(new LLUnaryNot(val));
        }
    }
}