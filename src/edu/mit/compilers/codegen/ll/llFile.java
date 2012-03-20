package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llFile implements llNode {
    llMethodDef main;
    ArrayList<llArrayDec> arrayDecs;
    ArrayList<llGlobalDec> globalDecs;
    ArrayList<llMethodDef> methods;
    ArrayList<llStringLiteral> strings;
    
    public llFile(llMethodDef mainMethod) {
        main = mainMethod;
        methods = new ArrayList<llMethodDef>();
        arrayDecs = new ArrayList<llArrayDec>();
        globalDecs = new ArrayList<llGlobalDec>();
        strings = new ArrayList<llStringLiteral>();
    }
    
    public llFile() {
        methods = new ArrayList<llMethodDef>();
        arrayDecs = new ArrayList<llArrayDec>();
        globalDecs = new ArrayList<llGlobalDec>();
        strings = new ArrayList<llStringLiteral>();
    }
    
    public void setMain(llMethodDef mainMethod) {
        main = mainMethod;
    }
    
    public void addMethod(llMethodDef method) {
        methods.add(method);
    }
    
    public void addArrayDec(llArrayDec dec) {
        arrayDecs.add(dec);
    }
    
    public void addGlobalDec(llGlobalDec dec) {
        globalDecs.add(dec);
    }
    
    public void addString(llStringLiteral str) {
        strings.add(str);
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
        main.accept(v);
        for (llMethodDef m : methods) {
            m.accept(v);
        }
        
        for (llStringLiteral s : strings) {
            s.accept(v);
        }
    }
}
