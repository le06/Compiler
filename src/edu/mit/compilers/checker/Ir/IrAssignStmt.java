package edu.mit.compilers.checker.Ir;

public class IrAssignStmt extends IrStatement {
    public IrAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }
    
    private IrLocation lhs;
    private IrExpression rhs;
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
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