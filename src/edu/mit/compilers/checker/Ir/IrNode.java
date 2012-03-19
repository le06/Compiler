package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public interface IrNode {
	public void accept(IrNodeVisitor v);
    public int getLineNumber();
    public int getColumnNumber();
    
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint);
    //public llNode getllRep(llLabel breakpoint, llLabel continuePoint);
}
