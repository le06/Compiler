package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

import edu.mit.compilers.checker.Ir.llExpression;
import edu.mit.compilers.checker.Ir.llLocation;

public class llAssign implements llNode {
    //private String literal;     // Location like "a" or "length" (from code)
    private llLocation target;
    private llExpression rhs;
    
    /**
     * Creates llAssign w/ default value location being the stack pointer
     * @param target
     *          Location (id)
     */
    public llAssign(llLocation location, llExpression expr) {
        target = location;
        rhs = expr;
    }
    
/*    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write(ASM_INSTR + "\t" +
                           valueLocation + ", " +
                           asmLocation + "\n"
                          );
    }*/

    @Override
    public void accept(llNodeVisitor v) {
        v.visit(this);
    }
}
