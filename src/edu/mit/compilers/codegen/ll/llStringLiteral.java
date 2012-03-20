package edu.mit.compilers.codegen.ll;

public class llStringLiteral implements llExpression {
    llLabel label;
    String literal;
    
    
    public llStringLiteral(String text, llLabel l) {
        label = l;
        literal = text;
    }
    
    public String getText() {
        return literal;
    }
    
    public String getLabelName() {
        return label.getName();
    }
    
    public String getLabelASM() {
        return label.getASMLabel();
    }
    
    @Override
    public void accept(llNodeVisitor v) {
        label.accept(v);
        v.visit(this);
    }
    
}
