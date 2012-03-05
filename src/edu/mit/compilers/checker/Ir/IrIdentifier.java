package edu.mit.compilers.checker.Ir;

public class IrIdentifier extends Ir implements IrExpression {
    public IrIdentifier(String name) {
        id = name;
    }
    
    private String id;

	public String getId() {
		return id;
	}
    
	@Override
	public void accept(IrNodeVisitor v) {
		// TODO Auto-generated method stub
		// do nothing! v never accepts this class.
	}

}