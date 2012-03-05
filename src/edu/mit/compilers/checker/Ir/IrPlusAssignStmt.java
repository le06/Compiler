package edu.mit.compilers.checker.Ir;

public class IrPlusAssignStmt extends IrStatement {
    public IrPlusAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }    
    
    private IrLocation lhs;
    private IrExpression rhs;
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}