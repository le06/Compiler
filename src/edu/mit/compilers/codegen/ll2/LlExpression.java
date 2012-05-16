package edu.mit.compilers.codegen.ll2;

public interface LlExpression extends LlNode {
    public static enum Type {
        VOID, BOOLEAN, INT;
    }
    public Type getType();
}