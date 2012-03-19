package edu.mit.compilers.codegen.ll;

public class llGlobalDec implements llNode {
    String id;
    
    public llGlobalDec(String var) {
        id = var;
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

}
