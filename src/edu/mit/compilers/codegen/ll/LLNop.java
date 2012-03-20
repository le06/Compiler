package edu.mit.compilers.codegen.ll;

public class LLNop implements LLNode, LLExpression {

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String addressOfResult() {
        return null;
    }

    @Override
    public void setAddress(String addr) {
    }

}
