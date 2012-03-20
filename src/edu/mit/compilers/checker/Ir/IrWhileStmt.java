package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llExpression;
import edu.mit.compilers.codegen.ll.llJump;
import edu.mit.compilers.codegen.ll.llJump.JumpType;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

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
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        llEnvironment out = new llEnvironment();
        
        llLabel w_start = new llLabel("w_start");
        llLabel w_end = new llLabel("w_end");
        
        llJump jump_end = new llJump(JumpType.NOT_EQUAL, w_end);
        llJump jump_start = new llJump(JumpType.UNCONDITIONAL, w_start);
        
        llExpression cond = (llExpression)condition.getllRep(null, null);
        llEnvironment code = (llEnvironment)block.getllRep(w_end, w_start);
        
        out.addNode(w_start);
        out.addNode(cond);
        out.addNode(jump_end);
        out.addNode(code);
        out.addNode(jump_start);
        out.addNode(w_end);
        
        return out;
    }
}