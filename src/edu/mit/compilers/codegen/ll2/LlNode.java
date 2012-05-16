package edu.mit.compilers.codegen.ll2;

public interface LlNode {
    public void accept(LlNodeVisitor v);
}