package edu.mit.compilers.checker.Ir;

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
}