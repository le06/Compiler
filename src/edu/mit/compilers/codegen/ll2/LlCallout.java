package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;
import java.util.Iterator;

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
    
    public String getNameWithoutQuotes() {
        return name.substring(1,name.length()-1);
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
    
    public String toString() {
        String args = "(";
        Iterator<LlNode> i = params.iterator();
        while (i.hasNext()) {
            LlNode next = i.next();
            if (next instanceof LlStringLiteral) {
                args = args + ((LlStringLiteral)next).getLabelName();
            } else {
                args = args + next.toString();
            }
            
            if (i.hasNext()) {
                args = args + ", ";
            }
        }
        args = args + ")";
        
        String funcName = getNameWithoutQuotes();
        return "CALLOUT: " + return_location.toString() + " = " + funcName + args;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
