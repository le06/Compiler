package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLJump implements LLNode {
    private JumpType type;
    private LLLabel label;
    private LLExpression cond;
    
    public LLJump(JumpType t, LLLabel loc) {
        type = t;
        label = loc;
    }
    
    public LLJump(LLExpression expr, LLLabel loc) {
        cond = expr;
        label = loc;
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
}
