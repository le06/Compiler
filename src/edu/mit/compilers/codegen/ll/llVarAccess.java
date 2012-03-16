package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llVarAccess implements llExpression {

    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        // Assuming this var access is part of an expression we
        // want to put it on the stack
        
    }*/

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }

}
