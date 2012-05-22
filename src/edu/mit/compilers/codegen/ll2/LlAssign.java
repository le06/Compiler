package edu.mit.compilers.codegen.ll2;

import edu.mit.compilers.codegen.ll2.LlConstant.Type;

public class LlAssign implements LlNode {

    private LlLocation lhsLoc;
    private LlLocation rhsLoc;
    private LlConstant rhsLit;
    
    private Type type; // null (i.e. irrelevant) if a = b; BOOLEAN/INT if a = !b;
    private boolean hasConstant;
    private boolean unaryOp; // if true, make sure to treat unary neg and unary not differently.
    
    public LlAssign(LlLocation lhs, LlLocation rhs, Type type, boolean unaryOp) {
        lhsLoc = lhs;
        rhsLoc = rhs;
        rhsLit = null;
        
        this.type = type;
        this.unaryOp = unaryOp;
        hasConstant = false;
    }
    
    public LlAssign(LlLocation lhs, LlConstant rhs, Type type, boolean unaryOp) {
        lhsLoc = lhs;
        rhsLoc = null;
        rhsLit = rhs;
        
        this.type = type;
        this.unaryOp = unaryOp;
        hasConstant = true;
    }
    
    public String toString() {
        if (hasConstant) {
            return lhsLoc.toString() + " = " + rhsLit.toString();
        } else {
            return lhsLoc.toString() + " = " + rhsLoc.toString();
        }
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
