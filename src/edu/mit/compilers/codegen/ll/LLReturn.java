package edu.mit.compilers.codegen.ll;

public class LLReturn implements LLNode {
    private LLExpression ret_val;
    private boolean has_return_value;
    
    public LLReturn() {
        has_return_value = false;
    }
    
    public LLReturn(LLExpression expr) {
        has_return_value = true;
        ret_val = expr;
    }
    
    public boolean hasReturn() {
        return has_return_value;
    }
    
    public LLExpression getExpr() {
    	return ret_val;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

}
