package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.llLabel;
import edu.mit.compilers.codegen.ll.llNode;

public class IrBlockStmt extends IrStatement {
	private IrBlock block;

	public IrBlock getBlock() {
		return block;
	}

	@Override
	public void accept(IrNodeVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString(int s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s; i++) {
            out.append(" ");
        }
        out.append(this.toString());
        return out.toString();
    }

    @Override
    public llNode getllRep(llLabel breakPoint, llLabel continuePoint) {
        return block.getllRep(breakPoint, continuePoint);
    }
}