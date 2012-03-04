package edu.mit.compilers.checker.Ir;


public abstract class Ir {
    private int line_num, col_num;
    
    public void addLocInfo(int line, int col) {
        line_num = line;
        col_num = col;
    }
    
    public int getLineNumber() {
        return line_num;
    }
    
    public int getColumnNumber() {
        return col_num;
    }
    
    // abstract walk()
}
