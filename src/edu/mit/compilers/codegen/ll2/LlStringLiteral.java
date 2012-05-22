package edu.mit.compilers.codegen.ll2;

public class LlStringLiteral implements LlConstant {

    private LlLabel label;
    private String text;
    
    public LlStringLiteral(LlLabel label, String text) {
        this.label = label;
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public String getTextWithoutQuotes() {
        return text.substring(1,text.length()-1);
    }
    
    public String getLabelName() {
        return label.getName();
    }
    
    public String getLabelASM() {
        return label.getASMLabel();
    }
    
    @Override
    public Type getType() {
        return Type.STRING;
    }

    public String toString() {
        return text;
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

    @Override
    public String print() {
        // string literals are only used as callout args.
        // this method should never be called.
        return null;
    }

}
