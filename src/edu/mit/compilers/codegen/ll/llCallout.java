package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llCallout implements llFunctionCall {
    private String fnName;
    private ArrayList<llExpression> params;
    
    public llCallout(String function) {
        fnName = function;
        params = new ArrayList<llExpression>();
    }
    
    public void addParam(llExpression param) {
        params.add(param);
    }
    
    public String getFnName() {
        return fnName;
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        for (int i = params.size() - 1; i >= 0; i--) {
            params.get(i).accept(v);
        }
        v.visit(this);
    }

}
