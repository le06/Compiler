package edu.mit.compilers.codegen.ll2;

public class LlBoolLiteral implements LlConstant {

    private boolean val;
    
    public LlBoolLiteral(boolean value) {
        val = value;
    }
    
    public boolean getValue() {
        return val;
    }
    
    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    public String toString() {
        return String.valueOf(val);
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }
    
    @Override
    public String print() {
        return "$" + String.valueOf(val);
    }

}