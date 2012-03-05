package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

public class IrMethodDecl extends IrMemberDecl {
	private IrType return_type;
	private IrIdentifier id;
	private ArrayList<IrVarDecl> args;
	private IrBlock block;
	
	public IrMethodDecl(IrType type, IrIdentifier name) {
	    return_type = type;
	    id = name;
	    args = new ArrayList<IrVarDecl>();
	}
	
	public void addArg(IrVarDecl arg) {
	    args.add(arg);
	}
	
	public void addBlock(IrBlock b) {
	    block = b;
	}
	
	public IrType getReturnType() {
		return return_type;
	}

	public IrIdentifier getId() {
		return id;
	}

	public ArrayList<IrVarDecl> getArgs() {
		return args;
	}

	public IrBlock getBlock() {
		return block;
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}
}