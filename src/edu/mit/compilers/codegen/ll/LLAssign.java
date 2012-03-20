package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

//import edu.mit.compilers.checker.Ir.llExpression;
//import edu.mit.compilers.checker.Ir.llLocation;

public class LLAssign implements LLNode {
    //private String literal;     // Location like "a" or "length" (from code)
    private LLLocation loc;
    private LLExpression expr;
    
    public LLAssign(LLLocation loc, LLExpression expr) {
        this.loc = loc;
        this.expr = expr;
    }
    
    @Override
    public void accept(LLNodeVisitor v) {
        v.visit(this);
    }
    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write(ASM_INSTR + "\t" +
                           valueLocation + ", " +
                           asmLocation + "\n"
                          );
    }*/
}
