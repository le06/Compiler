package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

public class LlMethodDecl implements LlNode {
    
    public enum MethodType {
        VOID,
        INT,
        BOOLEAN
    }
    
    private MethodType type;
    private String method_name;
    private ArrayList<LlTempLoc> args;
    private int num_args;
    private LlEnv method_code;
    
    public LlMethodDecl(MethodType t, String name, int num_args, LlEnv code) {
        type = t;
        method_name = name;
        args = new ArrayList<LlTempLoc>();
        this.num_args = num_args;
        method_code = code;
    }
    
    public MethodType getType() {
        return type;
    }
    
    public String getName() {
        return method_name;
    }
    
    public void addArg(String name) {
        args.add(new LlTempLoc(name));
    }
    
    public ArrayList<LlTempLoc> getArgs() {
        return args;
    }
    
    public int getNumArgs() {
        return num_args;
    }
    
    public LlEnv getEnv() {
        return method_code;
    }

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub
        
    }
    
}
