package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLVarLocation implements LLExpression, LLLocation {
    private String label;
    private Type type;
    private String location;
    private String temp_location;
    
    private int local_offset;
    
    // can be used as a location or an expression.
    public LLVarLocation(int offset, String label) {
    	this.local_offset = offset;
    	this.label = label;
    	this.type = null;
    	this.location = null;
    	this.temp_location = null;
    }
    
    public LLVarLocation(int offset, String label, Type type) {
    	this.local_offset = offset;
    	this.label = label;
    	this.type = type;
    	this.location = null;
    	this.temp_location = null;
    }
    
    public LLVarLocation(int offset, String label, Type type, String temp_location) {
    	this.local_offset = offset;
    	this.label = label;
    	this.type = type;
    	this.location = null;
    	this.temp_location = temp_location;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

    public int getBpOffset() {
    	return local_offset;
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
		return location;
	}

	@Override
	public void setLocation(String address) {
		location = address;
	}
	
	/*@Override
    public void writeASM(Writer outputStream) throws IOException {
        // Assuming this var access is part of an expression we
        // want to put it on the stack
    }*/
	
	@Override
    public String toString() {
        return label;
    }
}