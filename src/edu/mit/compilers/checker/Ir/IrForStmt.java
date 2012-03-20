package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLIntLiteral;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLJump.JumpType;
import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLLocation;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLVarLocation;
import edu.mit.compilers.codegen.ll.LLVarDecl;

public class IrForStmt extends IrStatement {
    public IrForStmt(IrIdentifier counter, IrExpression start_value,
            IrExpression stop_value, IrBlock block) {
        myCounter = counter;
        myStart_value = start_value;
        myStop_value = stop_value;
        myBlock = block;
    }

    private IrIdentifier myCounter;
    private IrExpression myStart_value;
    private IrExpression myStop_value;
    private IrBlock myBlock;

    public IrIdentifier getCounter() {
        return myCounter;
    }

    public IrExpression getStartValue() {
        return myStart_value;
    }

    public IrExpression getStopValue() {
        return myStop_value;
    }

    public IrBlock getBlock() {
        return myBlock;
    }

    @Override
    public void accept(IrNodeVisitor v) {
        v.visit(this);
    }

    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("FOR:\n");
        out.append(myCounter.toString(s + 1).concat("\n"));
        out.append(myStart_value.toString(s + 1).concat("\n"));
        out.append(myStop_value.toString(s + 1).concat("\n"));
        out.append(myBlock.toString(s + 1).concat("\n"));

        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLLabel for_begin = new LLLabel();
        LLLabel for_end = new LLLabel();
        
        LLJump jump_end = new LLJump(JumpType.EQUAL, for_end);
        LLJump jump_begin = new LLJump(JumpType.UNCONDITIONAL, for_begin);
        
        LLVarDecl dec = new LLVarDecl(myCounter.getId());
        LLLocation var = (LLLocation)(new LLVarLocation(myCounter.getId()));
        
        LLAssign init = new LLAssign(var,
                                    (LLExpression)myStart_value.getllRep(null, null));
        
        LLAssign incr = new LLAssign(var,
                               (LLExpression)(new LLBinaryOp((LLExpression)var,
                                                          (LLExpression)(new LLIntLiteral(1)),
                                                          IrBinOperator.PLUS)));
        
        LLExpression test = new LLBinaryOp((LLExpression)var,
                                         (LLExpression)myStop_value.getllRep(null, null),
                                         IrBinOperator.LT);
        
        LLEnvironment block = (LLEnvironment)myBlock.getllRep(for_end, for_begin);
        
        
        LLEnvironment out = new LLEnvironment();
        out.addNode(dec);
        out.addNode(init);
        out.addNode(for_begin);
        out.addNode(test);
        out.addNode(jump_end);
        out.addNode(block);
        out.addNode(incr);
        out.addNode(jump_begin);
        out.addNode(for_end);
        
        return out;
    }
}