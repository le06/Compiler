package edu.mit.compilers.codegen.ll;

public class LLMethodDecl implements LLNode {
	
    private String method_name;
    private LLEnvironment method_code;
    private int num_temps;
    
    public LLMethodDecl(String name) {
        method_name = name;
    }
    
    public LLMethodDecl(String name, LLEnvironment code) {
        method_name = name;
        method_code = code;
    }
    
    public String getName() {
        return method_name;
    }
    
    public void setNumTemps(int temps) {
        num_temps = temps;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
        method_code.accept(v);
    }
}
