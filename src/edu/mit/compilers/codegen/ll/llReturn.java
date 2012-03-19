package edu.mit.compilers.codegen.ll;

public class llReturn implements llNode {
    private llExpression retVal;
    private boolean hasReturnValue;
    
    public llReturn() {
        hasReturnValue = false;
    }
    
    public llReturn(llExpression expr) {
        hasReturnValue = true;
        retVal = expr;
    }
    
    public boolean hasReturn() {
        return hasReturnValue;
    }

    @Override
    public void accept(llNodeVisitor v) {
        if (hasReturnValue) {
            retVal.accept(v);
        }
        v.visit(this);
    }

}
