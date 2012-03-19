package edu.mit.compilers.codegen.ll;

public class llVarDec implements llNode {
    private String var_name;
    
    public llVarDec(String id) {
        var_name = id;
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

}
