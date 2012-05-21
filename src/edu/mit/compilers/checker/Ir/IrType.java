package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;

// same question.
public class IrType extends Ir {
    
    public IrType(Type type) {
        myType = type;
    }
    
    public Type myType;
    
    public enum Type {
        VOID, BOOLEAN, INT, MIXED;
    }

	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		v.visit(this);
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
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return new LLNop();
    }
}