package edu.mit.compilers.codegen.ll2;

public class LlArrayLoc implements LlLocation {

    public enum OffsetType {
        VARIABLE,
        CONSTANT
    }
    
    private String symbol;
    private String location;
    // the array index is either a constant offset, or a value that must be read from some address.
    private LlLocation offset_loc;
    private long offset_lit;
    private OffsetType type;
    
    public LlArrayLoc(String symbol, LlLocation offset) {
        this.symbol = symbol;
        offset_loc = offset;
        type = OffsetType.VARIABLE;
    }
    
    public LlArrayLoc(String symbol, long offset) {
        this.symbol = symbol;
        offset_lit = offset;
        type = OffsetType.CONSTANT;
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
    
    public OffsetType getOffsetType() {
        return type;
    }
    
    public LlLocation getOffsetLocation() {
        return offset_loc;
    }
    
    public long getOffsetLiteral() {
        return offset_lit;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub

    }

}
