package edu.mit.compilers.checker.Ir;

public class IrAssignStmt extends IrStatement {
    public IrAssignStmt(IrLocation loc, IrExpression expr) {
        lhs = loc;
        rhs = expr;
    }
    
    private IrLocation lhs;
    private IrExpression rhs;
}