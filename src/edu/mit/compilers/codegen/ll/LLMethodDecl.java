package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.codegen.ll.LLExpression.Type;

public class LLMethodDecl implements LLNode {
	
    private String method_name;
    private LLEnvironment method_code;
    private int num_temps;
    private Type type;
    
    public LLMethodDecl(String name, Type t) {
        method_name = name;
        type = t;
    }
    
    public LLMethodDecl(String name, Type t, LLEnvironment code) {
        method_name = name;
        method_code = code;
        type = t;
    }
    
    public String getName() {
        return method_name;
    }
    
    public void setNumTemps(int temps) {
        num_temps = temps;
    }
    
    public int getNumTemps() {
    	return num_temps;
    }
    
    public LLEnvironment getEnv() {
    	return method_code;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
}
