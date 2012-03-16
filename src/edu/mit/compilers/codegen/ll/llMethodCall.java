package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llMethodCall implements llFunctionCall {
    ArrayList<llExpression> params;
    
    public llMethodCall() {
        params = new ArrayList<llExpression>();
    }
    
    public void addParam(llExpression param) {
        params.add(param);
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        for (llExpression p : params) {
            p.accept(v);
        }
    }
}
