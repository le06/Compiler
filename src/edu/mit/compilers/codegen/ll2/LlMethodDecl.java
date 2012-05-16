package edu.mit.compilers.codegen.ll2;

import java.util.ArrayList;

import edu.mit.compilers.codegen.ll2.LlExpression.Type;

public class LlMethodDecl implements LlNode {

    private Type type;
    private String method_name;
    private ArrayList<LlTempLoc> args;
    private int num_args;
    private LlEnv method_code;
    
    public LlMethodDecl(Type t, String name, int num_args, LlEnv code) {
        type = t;
        method_name = name;
        args = new ArrayList<LlTempLoc>();
        this.num_args = num_args;
        method_code = code;
    }
    
    public Type getType() {
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
