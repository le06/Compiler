package edu.mit.compilers.codegen.ll2;

public class LlReturn implements LlNode {
    
    private LlLocation loc; // the address where the result is stored.
    private LlConstant lit; // a boolean or int constant.
    private boolean voidReturnType;
    private boolean hasConstant;
    
    public LlReturn() {
        voidReturnType = true;
        hasConstant = false;
    }
    
    public LlReturn(LlLocation loc) {
        this.loc = loc;
        voidReturnType = false;
        hasConstant = false;
    }
    
    // note that locations are also expressions.
    public LlReturn(LlConstant lit) {
        this.lit = lit;
        voidReturnType = false;
        hasConstant = true;
    }
    
    public LlLocation getLocation() {
        return loc;
    }
    
    public LlConstant getLiteral() {
        return lit;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
