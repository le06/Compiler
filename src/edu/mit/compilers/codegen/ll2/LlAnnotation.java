package edu.mit.compilers.codegen.ll2;

public class LlAnnotation implements LlNode {

    /*
     * Auxilliary LlNode class for annotating a series of LLIR nodes.
     * 
     * The following are annotated:
     * The start and end point of an if statement
     * "                        " a for statement
     * "                        " a while statement
     * 
     * The start and end point of a particular Boolean assignment of the form:
     *   a = b && c; OR a = b || c;
     *   
     * These statements are converted into:
     * if (!b) { a = 0; }
     * else    { a = c; }
     * 
     * if (b) { a = 1; }
     * else   { a = c; }
     * 
     * These constructs can be inconvenient during dataflow.
     * 
     * A pair of start and end annotations are uniquely identified by some String.
     * 
     */
    
    public enum AnnotationType {
        IF,
        FOR,
        WHILE,
        COND_OP_ASSIGNMENT
    }
    
    private AnnotationType type;
    private String id;
    private boolean isEnd; // if false, this marks the start of an annotation.
    private String annotation; // optional annotation. currently used for short circuit exprs.
    
    public LlAnnotation(AnnotationType type, String id, boolean isEnd, String annotation) {
        this.type = type;
        this.id = id;
        this.isEnd = isEnd;
        this.annotation = annotation;
    }
    
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    public String toString() {
        if (!isEnd) {
            return "START_ANTN: " + id;
        } else {
            return "END_ANTN: " + id;
        }
    }
    
    @Override
    public void accept(LlNodeVisitor v) {
        v.visit(this);
    }

}
