package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;

public interface IrNode {
	public void accept(IrNodeVisitor v);
    public int getLineNumber();
    public int getColumnNumber();
    
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint);
    //public llNode getllRep(llLabel breakpoint, llLabel continuePoint);
}
