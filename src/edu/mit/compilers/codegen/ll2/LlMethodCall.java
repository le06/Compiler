package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll2.LlMethodDecl.MethodType;

public class LlMethodCall implements LlNode {
    MethodType type;
    String name;
    ArrayList<LlNode> params; // locations or int/bool literals.
    
    private LlLocation return_location;
    private final String RAX = "%rax"; // the return address of any callout or method call, if applicable.
    
    public LlMethodCall(MethodType type, String name) {
        this.type = type;
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
