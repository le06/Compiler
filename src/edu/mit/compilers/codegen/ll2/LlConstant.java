package edu.mit.compilers.codegen.ll2;

public interface LlConstant extends LlNode {
    public static enum Type {
        BOOLEAN, INT, STRING
    }
    public Type getType();
    public String print();
}