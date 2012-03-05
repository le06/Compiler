package edu.mit.compilers.checker.Ir;

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
        out.append(return_expr.toString(s+1));
        return out.toString();
    }
}