package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLJump implements LLNode {
    private JumpType type;
    private LLLabel label;
    private LLExpression cond = null;
    private boolean jmp_when = false;
    
    public LLJump(JumpType t, LLLabel loc) {
        type = t;
        label = loc;
    }
    
    public LLJump(LLExpression expr, boolean bool, LLLabel loc) {
        cond = expr;
        jmp_when = bool;
        label = loc;
        if (bool) {
            type = JumpType.NOT_EQUAL;
        } else {
            type = JumpType.EQUAL;
        }
    }
    
    public static enum JumpType {
        UNCONDITIONAL,
        EQUAL,
        NOT_EQUAL,
        LT,
        GT,
        LEQ,
        GEQ;
    }
    
    public LLExpression getCond() {
    	return cond;
    }
    
    public boolean getJumpValue() {
    	return jmp_when;
    }
    
    public String getOpcode() {
        switch (this.type) {
        case UNCONDITIONAL:
            return "jmp";
        case EQUAL:
            return "je";
        case NOT_EQUAL:
            return "jne";
        case LT:
        	return "jl";
        case GT:
        	return "jg";
        case LEQ:
        	return "jle";
        case GEQ:
        	return "jge";
        default:
            return null;
        }
    }

    public LLLabel getLabel() {
    	return label;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write(getOpcode() + "\t" + label.getName());
    }*/
    
    @Override
    public String toString() {
        return getOpcode() + " " + label.toString();
    }
}
