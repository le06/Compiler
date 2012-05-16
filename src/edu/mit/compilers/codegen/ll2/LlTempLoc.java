package edu.mit.compilers.codegen.ll2;

public class LlTempLoc implements LlExpression, LlLocation {

    private Type type;
    private String symbol;
    private String location;
    
    public LlTempLoc(Type type, String symbol) {
        this.type = type;
        this.symbol = symbol;
    }

    @Override
    public Type getType() {
        return type;
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
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
