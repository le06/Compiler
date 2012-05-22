package edu.mit.compilers.codegen.ll2;

public class LlGlobalLoc implements LlLocation {

    private String symbol;
    private String location;
    
    public LlGlobalLoc(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
    
    @Override
    public String getLocation() {
        return location;
    }
    
    @Override
    public void setLocation(String loc) {
        location = loc;
    }
    
    public String toString() {
        return symbol;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
