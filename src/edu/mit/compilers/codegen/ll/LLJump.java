package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLJump implements LLNode {
    private JumpType type;
    private LLLabel label;
    
    public LLJump(JumpType t, LLLabel loc) {
        type = t;
        label = loc;
    }
    
    public static enum JumpType {
        UNCONDITIONAL,
        EQUAL,
        NOT_EQUAL,
        LESS_THAN,
        MORE_THAN;
    }
    
    public String getOpcode() {
        switch (this.type) {
        case UNCONDITIONAL:
            return "jmp";
        case EQUAL:
            return "je";
        case NOT_EQUAL:
            return "jne";
        case LESS_THAN:
        	return "jl";
        case MORE_THAN:
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
