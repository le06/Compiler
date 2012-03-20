package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLReturn;

public class IrReturnStmt extends IrStatement {
    public IrReturnStmt(IrExpression expr) {
        return_expr = expr;
    }
    
	private IrExpression return_expr;

	public IrExpression getReturnExpr() {
		return return_expr;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
	
	public String toString() {
	    return "return " + return_expr.toString();
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("return\n");
        if (return_expr != null) {
            out.append(return_expr.toString(s+1));
        }
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        if (return_expr == null) {
            return new LLReturn();
        } else {
            return new LLReturn((LLExpression)return_expr.getllRep(null, null));
        }
    }
}