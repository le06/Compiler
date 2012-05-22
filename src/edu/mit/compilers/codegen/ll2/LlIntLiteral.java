package edu.mit.compilers.codegen.ll2;

public class LlIntLiteral implements LlConstant {

    private long value;
    
    public LlIntLiteral(long value) {
        this.value = value;
    }
    
    public long getValue() {
        return value;
    }
    
    @Override
    public Type getType() {
        return Type.INT;
    }

    public String toString() {
        return String.valueOf(value);
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

    @Override
    public String print() {
        return "$" + String.valueOf(value);
    }

}
