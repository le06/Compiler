package edu.mit.compilers.codegen.ll2;

import edu.mit.compilers.checker.Ir.IrBinOperator;
import edu.mit.compilers.codegen.ll2.LlConstant.Type;

public class LlBinaryAssign implements LlNode {

    private LlLocation resultLoc;
    private LlLocation leftLoc, rightLoc;
    private LlConstant leftLit, rightLit;
    private Type type;
    private IrBinOperator op;
    
    private boolean leftIsConstant;
    private boolean rightIsConstant;
    
    // a = b OP c;
    public LlBinaryAssign(LlLocation resultLoc, LlLocation leftLoc, LlLocation rightLoc,
                          Type type, IrBinOperator op) {
        this.resultLoc = resultLoc;
        this.type = type;
        this.op = op;
        
        this.leftLoc = leftLoc;
        this.rightLoc = rightLoc;
        leftLit = null;
        rightLit = null;
        
        leftIsConstant = false;
        rightIsConstant = false;
    }
    
    // a = b OP const;
    public LlBinaryAssign(LlLocation resultLoc, LlLocation leftLoc, LlConstant rightLit,
                          Type type, IrBinOperator op) {
        this.resultLoc = resultLoc;
        this.type = type;
        this.op = op;
        
        this.leftLoc = leftLoc;
        this.rightLit = rightLit;
        leftLit = null;
        rightLoc = null;
        
        leftIsConstant = false;
        rightIsConstant = true;
    }
    
    // a = const OP c;
    public LlBinaryAssign(LlLocation resultLoc, LlConstant leftLit, LlLocation rightLoc,
                          Type type, IrBinOperator op) {
        this.resultLoc = resultLoc;
        this.type = type;
        this.op = op;
        
        this.leftLit = leftLit;
        this.rightLoc = rightLoc;
        leftLoc = null;
        rightLit = null;
        
        leftIsConstant = true;
        rightIsConstant = false;
    }
    
    // a = const1 OP const2;
    public LlBinaryAssign(LlLocation resultLoc, LlConstant leftLit, LlConstant rightLit,
                          Type type, IrBinOperator op) {
        this.resultLoc = resultLoc;
        this.type = type;
        this.op = op;
        
        this.leftLit = leftLit;
        this.rightLit = rightLit;
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
