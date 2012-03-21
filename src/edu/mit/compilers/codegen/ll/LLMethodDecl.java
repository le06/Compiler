package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.codegen.ll.LLExpression.Type;

public class LLMethodDecl implements LLNode {
	
    private String method_name;
    private LLEnvironment method_code;
    private int num_args;
    private int num_temps;
    private Type type;
    
    public LLMethodDecl(String name, Type t, int num_args) {
        method_name = name;
        type = t;
        this.num_args = num_args;
    }
    
    public LLMethodDecl(String name, Type t, int num_args, LLEnvironment code) {
        method_name = name;
        method_code = code;
        type = t;
        this.num_args = num_args;
    }
    
    public String getName() {
        return method_name;
    }
    
    public void setNumTemps(int temps) {
        num_temps = temps;
    }
    
    public int getNumArgs() {
    	return num_args;
    }
    
    public int getNumTemps() {
    	return num_temps;
    }
    
    public LLEnvironment getEnv() {
    	return method_code;
    }
    
    public Type getType() {
    	return type;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
