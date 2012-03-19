package edu.mit.compilers.codegen.ll;

public class llIntLiteral implements llExpression {
    private long value;
    
    public llIntLiteral(long val) {
        value = val;
    }
    
    public long getValue() {
        return value;
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
    
}
