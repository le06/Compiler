package edu.mit.compilers.codegen.ll;

public class llUnaryNot implements llExpression {
    llExpression negatedThing;
    
    public llUnaryNot(llExpression expr) {
        negatedThing = expr;
    }

/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write("imul $-1, ");
    }*/

    @Override
    public void accept(llNodeVisitor v) {
        negatedThing.accept(v);
        v.visit(this);
    }
}
