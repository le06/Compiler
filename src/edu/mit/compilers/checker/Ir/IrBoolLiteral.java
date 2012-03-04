package edu.mit.compilers.checker.Ir;

public class IrBoolLiteral extends IrLiteral {
    public IrBoolLiteral(boolean val) {
        literal = val;
    }
    
    private boolean literal;
}