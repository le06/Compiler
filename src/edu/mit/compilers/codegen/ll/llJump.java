package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llJump implements llNode {
    private JumpType type;
    private llLabel label;
    
    public llJump(JumpType t, llLabel loc) {
        type = t;
        label = loc;
    }
    
    public static enum JumpType {
        UNCONDITIONAL,
        EQUAL,
        NOT_EQUAL;
    }
    
    private String getOpcode() {
        switch (this.type) {
        case UNCONDITIONAL:
            return "jmp";
        case EQUAL:
            return "je";
        case NOT_EQUAL:
            return "jne";
        default:
            return null;
        }
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write(getOpcode() + "\t" + label.getName());
    }*/
}
