package edu.mit.compilers.codegen.ll2;

public class LlRegLoc implements LlLocation {

    // the name of the register is unique;
    // thus it serves as both a location in memory and a symbol.
    private String register;
    
    public LlRegLoc(String register) {
        this.register = register;
    }
    
    @Override
    public String getLocation() {
        return register;
    }

    @Override
    public String getSymbol() {
        return register;
    }

    @Override
    public void setLocation(String loc) {
        register = loc;
    }

    public String toString() {
        return register;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
