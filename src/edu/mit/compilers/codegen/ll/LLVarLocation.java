package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class LLVarLocation implements LLExpression, LLLocation {
    private String location;
    private Type type;
    private String temp_location;
    
    // can be used as a location or an expression.
    public LLVarLocation(String location, Type type) {
    	this.location = location;
    	this.type = type;
    	this.temp_location = null;
    }
    
    public LLVarLocation(String location, Type type, String temp_location) {
    	this.location = location;
    	this.type = type;
    	this.temp_location = temp_location;
    }

    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }

	@Override
	public String getLocation() {
		return location;
	}
    
	@Override
	public String addressOfResult() {
		return temp_location;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	/*@Override
    public void writeASM(Writer outputStream) throws IOException {
        // Assuming this var access is part of an expression we
        // want to put it on the stack
    }*/
	
}