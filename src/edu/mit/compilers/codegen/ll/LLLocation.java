package edu.mit.compilers.codegen.ll;

public interface LLLocation extends LLNode {
    public String getLabel();
    public String getLocation();
    public void setLocation(String address);
}
