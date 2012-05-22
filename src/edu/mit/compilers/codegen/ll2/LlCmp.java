package edu.mit.compilers.codegen.ll2;

public class LlCmp implements LlNode {

    private LlLocation leftLoc, rightLoc;
    private LlConstant leftLit, rightLit;
    
    private boolean leftIsConstant;
    private boolean rightIsConstant;
    
    // cmp locA, locB
    public LlCmp(LlLocation left, LlLocation right) {
        leftLoc = left;
        rightLoc = right;
        leftLit = null;
        rightLit = null;
        
        leftIsConstant = false;
        rightIsConstant = false;
    }
    
    // cmp locA, const
    public LlCmp(LlLocation left, LlConstant right) {
        leftLoc = left;
        rightLit = right;
        leftLit = null;
        rightLoc = null;
        
        leftIsConstant = false;
        rightIsConstant = true;
    }
    
    // cmp const, locB
    public LlCmp(LlConstant left, LlLocation right) {
        leftLit = left;
        rightLoc = right;
        leftLoc = null;
        rightLit = null;
        
        leftIsConstant = true;
        rightIsConstant = false;
    }
    // cmp const1, const2
    public LlCmp(LlConstant left, LlConstant right) {
        leftLit = left;
        rightLit = right;
        leftLoc = null;
        rightLoc = null;
        
        leftIsConstant = true;
        rightIsConstant = true;
    }
    
    // cmp 0, locB
    public LlCmp(LlLocation right) {
        leftLit = new LlIntLiteral(0);
        rightLoc = right;
        leftLoc = null;
        rightLit = null;
        
        leftIsConstant = true;
        rightIsConstant = false;
    }
    
    // cmp 0, litB
    public LlCmp(LlConstant right) {
        leftLit = new LlIntLiteral(0);
        rightLit = right;
        leftLoc = null;
        rightLoc = null;
        
        leftIsConstant = true;
        rightIsConstant = true;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
