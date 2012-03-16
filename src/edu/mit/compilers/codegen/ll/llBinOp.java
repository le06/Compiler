package edu.mit.compilers.codegen.ll;

public class llBinOp implements llExpression {
    llExpression l;
    llExpression r;

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        l.accept(v);
        r.accept(v);
    }

}
