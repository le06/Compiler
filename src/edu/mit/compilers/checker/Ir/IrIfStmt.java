package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llJump;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public class IrIfStmt extends IrStatement {
    public IrIfStmt(IrExpression test, IrBlock if_block, IrBlock else_block) {
        condition = test;
        true_block = if_block;
        false_block = else_block;
    }

    private IrExpression condition;
    private IrBlock true_block;
    private IrBlock false_block;

    public IrExpression getCondition() {
        return condition;
    }

    public IrBlock getTrueBlock() {
        return true_block;
    }

    public IrBlock getFalseBlock() {
        return false_block;
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
        out.append("IF:\n");
        out.append(condition.toString(s + 1).concat("\n"));
        out.append(true_block.toString(s+1).concat("\n"));
        if (false_block != null) {
            out.append(false_block.toString(s+1).concat("\n"));
        }

        return out.toString();
    }

    @Override
    public llNode getllRep() {
        llEnvironment currentEnvironment = new llEnvironment();
        
        llLabel true_label = new llLabel();
        llLabel if_end = new llLabel();
        
        llJump jump_true = new llJump(llJump.JumpType.NOT_EQUAL, true_label);
        llJump end_false = new llJump(llJump.JumpType.UNCONDITIONAL, if_end);
        
        llEnvironment eval_cond_env = (llEnvironment)condition.getllRep();
        llEnvironment true_env = (llEnvironment)true_block.getllRep();
        llEnvironment false_env = (llEnvironment)false_block.getllRep();
        
        currentEnvironment.addNode(eval_cond_env);
        currentEnvironment.addNode(jump_true);
        currentEnvironment.addNode(false_env);
        currentEnvironment.addNode(end_false);
        currentEnvironment.addNode(true_label);
        currentEnvironment.addNode(true_env);
        currentEnvironment.addNode(if_end);
        
        return currentEnvironment;
    }
}