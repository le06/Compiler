package edu.mit.compilers.codegen.ll;

public class llMethodDef implements llNode {
    String methodName;
    llEnvironment methodCode;
    
    public llMethodDef(String name) {
        methodName = name;
    }
    
    public llMethodDef(String name, llEnvironment code) {
        methodName = name;
        methodCode = code;
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        methodCode.accept(v);
    }
}
