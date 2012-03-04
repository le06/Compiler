package edu.mit.compilers.checker.Ir;

public class IrWhileStmt extends IrStatement {
    public IrWhileStmt(IrExpression test, IrBlock true_block) {
        condition = test;
        block = true_block;
    }
    
    private IrExpression condition;
    private IrBlock block;
}