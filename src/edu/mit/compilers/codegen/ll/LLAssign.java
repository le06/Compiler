package edu.mit.compilers.codegen.ll;

import java.io.IOException;
import java.io.Writer;

//import edu.mit.compilers.checker.Ir.llExpression;
//import edu.mit.compilers.checker.Ir.llLocation;

public class LLAssign implements LLNode {
    //private String literal;     // Location like "a" or "length" (from code)
								  // Can implement later for debugging.
    private LLLocation loc;
	private LLExpression expr;
	boolean dead = false;
    
    public LLLocation getLoc() {
		return loc;
	}

	public LLExpression getExpr() {
		return expr;
	}
	
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
    
    public String toString() {
        return loc.toString() + " = " + expr.toString();
    }
    
    public void kill() {
        dead = true;
    }
    
    public boolean isDead() {
        return dead;
    }
}
