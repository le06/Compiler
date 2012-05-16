package edu.mit.compilers.codegen.ll2;

public class LlIntLiteral implements LlExpression {

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

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
