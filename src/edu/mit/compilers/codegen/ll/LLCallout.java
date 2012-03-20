package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class LLCallout implements LLExpression {
    private String fn_name;
    private ArrayList<LLExpression> params;
    
    private String temp_location;
    
    // can be called as part of an expression or not.
    public LLCallout(String fn_name) {
        this.fn_name = fn_name;
        params = new ArrayList<LLExpression>();
        this.temp_location = null;
    }
    
    public LLCallout(String fn_name, String temp_location) {
        this.fn_name = fn_name;
        params = new ArrayList<LLExpression>();
        this.temp_location = temp_location;
    }
    
    public void addParam(LLExpression param) {
        params.add(param);
    }
    
    public String getFnName() {
        return fn_name;
    }
    
    public ArrayList<LLExpression> getParams() {
		return params;
	}

	@Override
    public void accept(LLNodeVisitor v) {
        for (int i = 1; i < params.size(); i++) {
            params.get(i).accept(v);
        }
        v.visit(this);
    }

	@Override
	public String addressOfResult() {
		return temp_location;
	}

	@Override
	public Type getType() {
		return Type.INT;
	}

    @Override
    public void setAddress(String addr) {
        temp_location = addr;
    }

}