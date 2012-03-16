package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llFile implements llNode {
    llEnvironment main;
    ArrayList<llEnvironment> methods;
    ArrayList<llStringLiteral> strings;
    
    public llFile(llEnvironment mainMethod) {
        main = mainMethod;
        methods = new ArrayList<llEnvironment>();
    }
    
    public llFile() {
        methods = new ArrayList<llEnvironment>();
    }
    
    public void setMain(llEnvironment mainMethod) {
        main = mainMethod;
    }
    
    public void addMethod(llEnvironment method) {
        methods.add(method);
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        main.accept(v);
        for (llEnvironment m : methods) {
            m.accept(v);
        }
        
        for (llStringLiteral s : strings) {
            s.accept(v);
        }
    }
}
