package edu.mit.compilers.checker.Ir;

public class IrAssignStmt extends IrStatement {
    public IrAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }
    
    private IrLocation lhs;
    private IrExpression rhs;
    
	public IrLocation getLeft() {
		return lhs;
	}
	public IrExpression getRight() {
		return rhs;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);		
	}
    
/*    @Override
    public String toString(int spaces_before) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < spaces_before; i++) {
            out.append(" ");
        }
        
        out.append("=\n");
        out.append(lhs.toString(spaces_before+1));
        out.append(rhs.toString(spaces_before+1));
        return out.toString();
    }*/
}