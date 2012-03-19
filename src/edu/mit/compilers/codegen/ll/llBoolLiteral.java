package edu.mit.compilers.codegen.ll;

public class llBoolLiteral implements llNode {
    private boolean val;
    
    public llBoolLiteral(boolean value) {
        val = value;
    }
    
    public boolean getValue() {
        return val;
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

}
