package edu.mit.compilers.checker.Ir;

public class IrPlusAssignStmt extends IrStatement {
    public IrPlusAssignStmt(IrLocation loc, IrExpression expr) {
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
}