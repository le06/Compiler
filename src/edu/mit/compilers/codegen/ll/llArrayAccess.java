package edu.mit.compilers.codegen.ll;

public class llArrayAccess implements llNode {

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
}
