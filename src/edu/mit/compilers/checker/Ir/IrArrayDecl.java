package edu.mit.compilers.checker.Ir;

public class IrArrayDecl extends IrGlobalDecl {
	private IrIdentifier id;
	private IrIntLiteral array_size;
	
	public IrArrayDecl(IrIdentifier name, IrIntLiteral size) {
	    id = name;
	    array_size = size;
	}

	public IrIdentifier getId() {
		return id;
	}

	public IrIntLiteral getArraySize() {
		return array_size;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}