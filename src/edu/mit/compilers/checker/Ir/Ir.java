package edu.mit.compilers.checker.Ir;

public abstract class Ir {
    private Ir left;
    private Ir right;
    
    public Ir getLeft() {
        return left;
    }
    
    public Ir getRight() {
        return right;
    }
}
