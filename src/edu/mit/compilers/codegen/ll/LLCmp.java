package edu.mit.compilers.codegen.ll;

public class LLCmp implements LLNode {

    private LLExpression l, r;
    
    public LLCmp(LLExpression left, LLExpression right) {
        l = left;
        r = right;
    }
    
    public LLCmp(LLVarLocation right) {
    	l = new LLIntLiteral(0);
    	r = right;
    }
    
    public LLExpression getL() {
    	return l;
    }
    
    public LLExpression getR() {
    	return r;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
