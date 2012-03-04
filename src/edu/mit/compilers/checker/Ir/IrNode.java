package edu.mit.compilers.checker.Ir;

public interface IrNode {
	public void accept(IrNodeVisitor v);
}
