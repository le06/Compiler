package edu.mit.compilers.codegen.ll;

public class llArrayDec implements llNode {

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
}
