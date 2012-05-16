package edu.mit.compilers.codegen.ll2;

public class LlAssign implements LlNode {

    private LlLocation loc;
    private LlExpression expr;
    
    public LlAssign(LlLocation loc, LlExpression expr) {
        this.loc = loc;
        this.expr = expr;
    }
    
    public LlLocation getLoc() {
        return loc;
    }

    public LlExpression getExpr() {
        return expr;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
