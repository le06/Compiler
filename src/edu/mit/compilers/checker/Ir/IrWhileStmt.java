package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLJump.JumpType;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public class IrWhileStmt extends IrStatement {
    public IrWhileStmt(IrExpression test, IrBlock true_block) {
        condition = test;
        block = true_block;
    }

    private IrExpression condition;
    private IrBlock block;

    public IrExpression getCondition() {
        return condition;
    }

    public IrBlock getBlock() {
        return block;
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
        out.append("WHILE:\n");
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(condition.toString().concat("\n"));

        out.append(block.toString(s+1).concat("\n"));

        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLEnvironment out = new LLEnvironment();
        
        LLLabel w_start = new LLLabel("w_start");
        LLLabel w_end = new LLLabel("w_end");
        
        //LLJump jump_end = new LLJump(JumpType.NOT_EQUAL, w_end);
        
        
        LLExpression cond = (LLExpression)condition.getllRep(null, null);
        LLEnvironment code = (LLEnvironment)block.getllRep(w_end, w_start);
        
        LLJump jump_end = new LLJump(cond, false, w_end);
        LLJump jump_start = new LLJump(JumpType.UNCONDITIONAL, w_start);
        
        out.addNode(w_start);
        //out.addNode(cond);
        out.addNode(jump_end);
        out.addNode(code);
        out.addNode(jump_start);
        out.addNode(w_end);
        
        return out;
    }
}