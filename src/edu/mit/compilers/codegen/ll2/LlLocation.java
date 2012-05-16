package edu.mit.compilers.codegen.ll2;

public interface LlLocation extends LlExpression {
    public String getSymbol();
    public String getLocation();
    public void setLocation(String loc);
}
