package edu.mit.compilers.codegen.ll;

public class LLArrayLocation implements LLExpression, LLLocation {
	private String label;
	private Type type;
	private String temp_location;

	private long size;
	private LLExpression index;
    
	// can be used as a location or an expression.
	public LLArrayLocation(String label, LLExpression index) {
		this.label = label;
		this.type = null;
		this.temp_location = null;
		this.index = index;
	}
	
	public LLArrayLocation(String label, Type type,
						   long size, LLExpression index) {
		this.label = label;
		this.type = type;
		this.temp_location = null;
		this.size = size;
		this.index = index;
	}
	
	public LLArrayLocation(String label, Type type, String temp_location,
						   long size, LLExpression index) {
		this.label = label;
		this.type = type;
		this.temp_location = temp_location;
		this.size = size;
		this.index = index;
	}

	public long getSize() {
		return size;
	}
	
	public LLExpression getIndexExpr() {
		return index;
	}
	
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

	@Override
	public String getLabel() {
		return label;
	}    
    
	@Override
	public String addressOfResult() {
		return temp_location;
	}

	@Override
	public Type getType() {
		return type;
	}

    @Override
    public void setAddress(String addr) {
        temp_location = addr;
    }

	@Override
	public String getLocation() {
		return label;
	}

	@Override
	public void setLocation(String address) {
		label = address;
	}

}
