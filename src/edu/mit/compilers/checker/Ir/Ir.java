package edu.mit.compilers.checker.Ir;


public abstract class Ir {
    private Ir left;
    private Ir right;
    private int line;
    
    public Ir getLeft() {
        return left;
    }
    
    public Ir getRight() {
        return right;
    }
    
    // abstract walk()
}
