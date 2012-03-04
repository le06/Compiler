package edu.mit.compilers.checker.Ir;

public class IrIfStmt extends IrStatement {
    public IrIfStmt (IrExpression test, IrBlock if_block, IrBlock else_block) {
        condition = test;
        true_block = if_block;
        false_block = else_block;
    }
    
    private IrExpression condition;
    private IrBlock true_block;
    private IrBlock false_block;
}