package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll2.LlMethodDecl.MethodType;

public class LlCallout implements LlNode {
    MethodType type = MethodType.INT;
    String name;
    ArrayList<LlNode> params; // locations or int/bool/string literals.

    private LlLocation return_location;
    private final String RAX = "%rax"; // the return address of any callout or method call, if applicable.
    
    public LlCallout(String name) {
        this.name = name;
        params = new ArrayList<LlNode>();
        
        return_location = new LlRegLoc(RAX);
    }
    
    public MethodType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public LlLocation getReturnLocation() {
        return return_location;
    }
    
    public void addParam(LlNode param) {
        params.add(param);
    }
    
    public ArrayList<LlNode> getParams() {
        return params;
    }
    
    public int getNumParams() {
        return params.size();
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
