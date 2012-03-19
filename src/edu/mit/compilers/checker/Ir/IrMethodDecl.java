package edu.mit.compilers.checker.Ir;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll.llEnvironment;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llMethodDef;
import edu.mit.compilers.codegen.ll.llNode;

public class IrMethodDecl extends IrMemberDecl {
	private IrType return_type;
	private IrIdentifier id;
	private ArrayList<IrParameterDecl> params;
	private IrBlock block;
	
	public IrMethodDecl(IrType type, IrIdentifier name) {
	    return_type = type;
	    id = name;
	    params = new ArrayList<IrParameterDecl>();
	}
	
	public void addArg(IrParameterDecl arg) {
	    params.add(arg);
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

	public ArrayList<IrParameterDecl> getParams() {
		return params;
	}

	public IrBlock getBlock() {
		return block;
	}
	
	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

    @Override
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append("Method:\n");
        out.append(return_type.toString(s+1).concat("\n"));
        out.append(id.toString(s+1).concat("\n"));
        for (IrParameterDecl d : params) {
            out.append(d.toString(s+1).concat("\n"));
        }
        
        out.append(block.toString(s+1).concat("\n"));
        return out.toString();
    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        llEnvironment code = (llEnvironment)block.getllRep(null, null);
        return new llMethodDef(id.getId(), code);
    }
}