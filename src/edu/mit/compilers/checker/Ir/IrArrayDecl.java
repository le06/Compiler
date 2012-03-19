package edu.mit.compilers.checker.Ir;

import javax.management.RuntimeErrorException;

import edu.mit.compilers.codegen.ll.llArrayDec;
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public class IrArrayDecl extends IrGlobalDecl {
	private IrIdentifier id;
	private IrIntLiteral array_size;
	
	public IrArrayDecl(IrIdentifier name, IrIntLiteral size) {
	    if (size.isNegative()) {
	        throw new RuntimeException("Array cannot have negative size");
	    }
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
	
	public String toString() {
	    return id.toString() + "[" + array_size.toString() + "]";
	}

    @Override
    public String toString(int spaces_before) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < spaces_before; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        try {
            return new llArrayDec(id.toString(), array_size.getIntRep());
        } catch (NumberFormatException e) {
            System.err.println("IrIntRep array size check failed");
            System.exit(-1);
            return null;
        }
    }
}