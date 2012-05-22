package edu.mit.compilers.codegen.ll2;

public class LlArrayLoc implements LlLocation {
    
    private String symbol;
    private String location;
    // the array index is either a constant offset, or a value that must be read from some address.
    private LlLocation offset_loc;
    private long offset_lit;
    private boolean hasConstant;
    
    public LlArrayLoc(String symbol, LlLocation offset) {
        this.symbol = symbol;
        offset_loc = offset;
        hasConstant = false;
    }
    
    public LlArrayLoc(String symbol, long offset) {
        this.symbol = symbol;
        offset_lit = offset;
        hasConstant = true;
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
    
    public LlLocation getOffsetLocation() {
        return offset_loc;
    }
    
    public long getOffsetLiteral() {
        return offset_lit;
    }
    
    public String toString() {
        if (!hasConstant) {
            return symbol + "[" + offset_loc.toString() + "]";
        } else {
            return symbol + "[" + offset_lit + "]";
        }
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
