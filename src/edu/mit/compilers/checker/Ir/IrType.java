package edu.mit.compilers.checker.Ir;

// same question.
public class IrType extends Ir {
    
    public IrType(Type type) {
        myType = type;
    }
    
    Type myType;
    
    public enum Type {
        VOID, BOOLEAN, INT;
    }
}