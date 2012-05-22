package edu.mit.compilers.codegen.ll2;

public class LlJmp implements LlNode {

    public static enum JumpType {
        UNCONDITIONAL,
        EQUAL,
        NOT_EQUAL,
        LT,
        GT,
        LEQ,
        GEQ;
    }
    
    private JumpType type;
    private LlLabel label;
    
    public LlJmp(JumpType t, LlLabel l) {
        type = t;
        label = l;
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
    
    public LlLabel getLabel() {
        return label;
    }
    
    public String toString() {
        String op = getOpcode().toUpperCase();
        return op + ": " + label.getName();
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
