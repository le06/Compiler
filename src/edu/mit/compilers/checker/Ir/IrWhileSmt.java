package edu.mit.compilers.checker.Ir;

public class IrWhileSmt extends IrStatement {
    public IrWhileSmt(IrExpression test, IrBlock true_block) {
        condition = test;
        block = true_block;
    }
    
    private IrExpression condition;
    private IrBlock block;
}