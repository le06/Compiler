package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

public class llAssign extends llNode {
    private String literal;     // Location like "a" or "length" (from code)
    private String asmLocation; // Location like "%rax" or "-8(%rbp)"
    private String valueLocation; // Normally "0(%rsp)"
    
    private final String ASM_INSTR = "mov";
    private final String DEFAULT_LOC = "0(%rsp)";
    
    /**
     * Creates llAssign w/ default value location being the stack pointer
     * @param target
     */
    public llAssign(String target) {
        literal = target;
        valueLocation = DEFAULT_LOC;
    }
    
    public void setValueLocation(String loc) {
        valueLocation = loc;
    }
    
    public void setTargetLocation(String loc) {
        asmLocation = loc;
    }
    
    public String getLiteralName() {
        return literal;
    }
    
    @Override
    public void writeASM(Writer outputStream) throws IOException {
        outputStream.write(ASM_INSTR + "\t" +
                           valueLocation + ", " +
                           asmLocation + "\n"
                          );
    }
}
