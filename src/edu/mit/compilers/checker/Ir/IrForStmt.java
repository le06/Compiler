package edu.mit.compilers.checker.Ir;

public class IrForStmt extends IrStatement {
    public IrForStmt (IrIdentifier counter, IrExpression start_value, 
                      IrExpression stop_value, IrBlock block) {
        myCounter = counter;
        myStart_value = start_value;
        myStop_value = stop_value;
        myBlock = block;
    }
    
    private IrIdentifier myCounter;
    private IrExpression myStart_value;
    private IrExpression myStop_value;
    private IrBlock myBlock;
}