package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;
import java.util.HashSet;

import edu.mit.compilers.codegen.ll.LLExpression.Type;

public class LLMethodDecl implements LLNode {
	
    private String method_name;
    private LLEnvironment method_code;
    private int num_args;
    private int num_temps;
    private Type type;
    private ArrayList<LLVarLocation> args = new ArrayList<LLVarLocation>();
    private HashSet<String> registersUsed;
    
    public LLMethodDecl(String name, Type t, int num_args) {
        method_name = name;
        type = t;
        this.num_args = num_args;
        registersUsed = new HashSet<String>();
    }
    
    public LLMethodDecl(String name, Type t, int num_args, LLEnvironment code) {
        method_name = name;
        method_code = code;
        type = t;
        this.num_args = num_args;
        registersUsed = new HashSet<String>();
    }
    
    public void addArg(String name) {
    	args.add(new LLVarLocation(0, name));
    }
    
    public ArrayList<LLVarLocation> getArgs() {
    	return args;
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
    
    public void addReg(String reg) {
    	registersUsed.add(reg);
    }
    
    public boolean usesRegister(String reg) {
    	return registersUsed.contains(reg);
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
    @Override
    public String toString() {
        return method_name;
    }
}
