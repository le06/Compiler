package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

// same question.
public class IrType extends Ir {
    
    public IrType(Type type) {
        myType = type;
    }
    
    Type myType;
    
    public enum Type {
        VOID, BOOLEAN, INT, MIXED;
    }

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// do nothing! v never accepts this class.
	}
	
	public String toString() {
	    return myType.toString();
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        return null;
    }
}