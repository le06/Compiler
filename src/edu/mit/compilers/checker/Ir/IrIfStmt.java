package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLEnvironment;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLJump;
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLEnvironment currentEnvironment = new LLEnvironment();
        
        LLLabel true_label = new LLLabel("true");
        LLLabel if_end = new LLLabel("endif");
        
        LLExpression eval_cond_env = (LLExpression)condition.getllRep(null, null);
        LLEnvironment true_env = (LLEnvironment)true_block.getllRep(breakPoint, continuePoint);
        
        if (false_block != null) {
        	LLEnvironment false_env = (LLEnvironment)false_block.getllRep(breakPoint, continuePoint);
        	
            LLJump jump_true = new LLJump(eval_cond_env, true, true_label);
            LLJump end_false = new LLJump(LLJump.JumpType.UNCONDITIONAL, if_end);
        	
            //currentEnvironment.addNode(eval_cond_env);
            currentEnvironment.addNode(jump_true);
            currentEnvironment.addNode(false_env);
            currentEnvironment.addNode(end_false);
            currentEnvironment.addNode(true_label);
            currentEnvironment.addNode(true_env);
            currentEnvironment.addNode(if_end);
        	
        } else {
        	// i.e. SKIP PAST THE IF if the cond is false.
        	LLJump jump_false = new LLJump(eval_cond_env, false, if_end);
        	
            //currentEnvironment.addNode(eval_cond_env);
            currentEnvironment.addNode(jump_false);
            currentEnvironment.addNode(true_label); // not strictly necessary.
            currentEnvironment.addNode(true_env);
            currentEnvironment.addNode(if_end);
        }
        
        return currentEnvironment;
    }
}