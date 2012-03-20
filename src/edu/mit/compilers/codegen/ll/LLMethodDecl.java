package edu.mit.compilers.codegen.ll;

public class LLMethodDecl implements LLNode {
	
    String method_name;
    LLEnvironment method_code;
    
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
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
        method_code.accept(v);
    }
}
