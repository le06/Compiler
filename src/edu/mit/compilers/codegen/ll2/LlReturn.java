package edu.mit.compilers.codegen.ll2;

public class LlReturn implements LlNode {

    public enum ReturnType {
        VOID,
        VARIABLE,
        CONSTANT
    }
    
    private LlLocation loc; // the address where the result is stored.
    private LlExpression expr; // a literal constant.
    private ReturnType type;
    
    public LlReturn() {
        type = ReturnType.VOID;
    }
    
    public LlReturn(LlLocation loc) {
        this.loc = loc;
        type = ReturnType.VARIABLE;
    }
    
    // note that locations are also expressions.
    public LlReturn(LlExpression expr) {
        this.expr = expr;
        type = ReturnType.CONSTANT;
    }
    
    public ReturnType getReturnType() {
        return type;
    }
    
    public LlLocation getLocation() {
        return loc;
    }
    
    public LlExpression getExpression() {
        return expr;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
