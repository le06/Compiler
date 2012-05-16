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
    
    public String getLabelName() {
        return label.getName();
    }
    
    public String getLabelASM() {
        return label.getASMLabel();
    }
    
    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return Type.STRING;
    }

    @Override
    public void accept(LlNodeVisitor v) {
        // TODO Auto-generated method stub
        
    }

}
