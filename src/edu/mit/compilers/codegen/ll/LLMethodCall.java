package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLMethodCall implements LLExpression {
    ArrayList<LLExpression> params;
    String method;
    Type type;
    
    String temp_location;
    
    // can be called as part of an expression or not.
    public LLMethodCall(String method, Type type) {
        this.method = method;
        this.type = type;
        params = new ArrayList<LLExpression>();
        
        this.temp_location = null;
    }
    
    public LLMethodCall(String method, Type type, String temp_location) {
        this.method = method;
        this.type = type;
        params = new ArrayList<LLExpression>();
        
        this.temp_location = temp_location;
    }
    
    public void addParam(LLExpression param) {
        params.add(param);
    }
    
    public int getNumParams() {
        return params.size();
    }
    
    public String getMethodName() {
        return method;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        for (LLExpression p : params) {
            p.accept(v);
        }
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		return temp_location;
	}

	@Override
	public Type getType() {
		return type;
	}
}
