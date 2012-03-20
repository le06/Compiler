package edu.mit.compilers.checker.Ir;

<<<<<<< HEAD
import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;
import edu.mit.compilers.codegen.ll.llStringLiteral;
=======
import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLStringLiteral;
>>>>>>> 5c223d490eac039adfa26ffb308b8d6aa47fa082

public class IrStringArg extends IrCalloutArg {
    public IrStringArg(IrStringLiteral argument) {
        arg = argument;
    }
    
	private IrStringLiteral arg;

	@Override
	public void accept(IrNodeVisitor v) {
		// no need to visit. scanner enforces correctness
	}
	
	public String toString() {
	    return arg.toString();
	}
	
    public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        LLLabel strLabel = new LLLabel("strlit");
        return new LLStringLiteral(arg.toString(), strLabel);
    }
}