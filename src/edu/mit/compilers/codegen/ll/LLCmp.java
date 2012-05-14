package edu.mit.compilers.codegen.ll;

public class LLCmp implements LLNode {

    private LLVarLocation l, r;
    
    public LLCmp(LLVarLocation left, LLVarLocation right) {
        l = left;
        r = right;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
