package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llCallout implements llFunctionCall {
    private String fnName;
    private ArrayList<String> paramLocations;
    
    public llCallout() {
        paramLocations = new ArrayList<String>();
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

}
