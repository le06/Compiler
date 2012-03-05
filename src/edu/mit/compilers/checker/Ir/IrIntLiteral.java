package edu.mit.compilers.checker.Ir;

public class IrIntLiteral extends IrLiteral {
    public IrIntLiteral(String value, Type type) {
        representation = value;
        num_type = type;
    }
    
    private String representation;
    private Type num_type;
    
    public enum Type {
        DECIMAL,
        HEX,
        BINARY;
    }
}