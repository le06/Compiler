package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llUnaryNeg implements llNode {
    llExpression negatedThing;

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
