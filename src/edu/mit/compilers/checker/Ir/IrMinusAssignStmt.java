package edu.mit.compilers.checker.Ir;

public class IrMinusAssignStmt extends IrStatement {
    public IrMinusAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }    
    
    private IrLocation lhs;
    private IrExpression rhs;
}