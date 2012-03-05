package edu.mit.compilers.checker.Ir;

public interface IrNode {
	public void accept(IrNodeVisitor v);
    public int getLineNumber();
    public int getColumnNumber();
}
