package edu.mit.compilers.checker.Ir;

public class IrBinopExpr extends Ir implements IrExpression {
    public IrBinopExpr(IrBinOperator op, IrExpression left, IrExpression right) {
        operator = op;
        lhs = left;
        rhs = right;
    }
    
    private IrBinOperator operator;
    private IrExpression lhs;
    private IrExpression rhs;
    

    public String toString() {
        return lhs.toString() + " " + operator.toString() + " " + rhs.toString();
    }

	@Override
	public IrType getExprType(IrNodeChecker c) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void accept(IrNodeVisitor v) {
		lhs.accept(v);
		rhs.accept(v);
		v.visit(this);
	}
	
	   public String toString (int s) {
	        StringBuilder out = new StringBuilder();
	        for (int i = 0; i < s; i++) {
	            out.append(" ");
	        }
	        out.append(operator.toString().concat("\n"));
	        for (int i = 0; i < s; i++) {
                out.append(" ");
            }
	        out.append(lhs.toString(s+1));
	        
	        for (int i = 0; i < s; i++) {
                out.append(" ");
            }
	        out.append(rhs.toString(s+1));
	        
	        return out.toString();
	    }
}