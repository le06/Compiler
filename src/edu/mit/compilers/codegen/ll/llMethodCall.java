package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llMethodCall implements llFunctionCall {
    ArrayList<llExpression> params;
    String method;
    
    public llMethodCall(String methodName) {
        method = methodName;
        params = new ArrayList<llExpression>();
    }
    
    public void addParam(llExpression param) {
        params.add(param);
    }
    
    public int getNumParams() {
        return params.size();
    }
    
    public String getMethodName() {
        return method;
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        for (llExpression p : params) {
            p.accept(v);
        }
        v.visit(this);
    }
}
