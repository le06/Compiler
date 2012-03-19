package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llReturn;

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
    public llNode getllRep() {
        if (return_expr == null) {
            return new llReturn();
        } else {
            return new llReturn((llExpression)return_expr.getllRep());
        }
    }
}