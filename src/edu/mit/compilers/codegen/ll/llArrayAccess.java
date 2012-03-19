package edu.mit.compilers.codegen.ll;

public class llArrayAccess implements llNode, llLocation {
    private int baseOffset;
    private int arraySize;
    private String baseLocation;
    
    private String var_name;
    private llExpression index;
    
    public llArrayAccess(String id, llExpression expr) {
        var_name = id;
        index = expr;
    }
    
    public String getAccessLocation(int i) {
        return (baseOffset + i) + "(" + baseLocation + ")";
    }

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

    @Override
    public String getLocationStr() {
        return baseOffset + "(" + baseLocation + ")";
    }
}
