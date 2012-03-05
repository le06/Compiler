package edu.mit.compilers.checker.Ir;

public class IrBlockStmt extends IrStatement {
	private IrBlock block;

	public IrBlock getBlock() {
		return block;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		
	}
}