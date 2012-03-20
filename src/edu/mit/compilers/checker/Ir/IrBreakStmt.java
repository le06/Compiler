package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.codegen.ll.LLLabel;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLNop;

public class IrBreakStmt extends IrStatement {

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
        out.append("break");
        return out.toString();
    }

    @Override
    public LLNode getllRep(LLLabel breakPoint, LLLabel continuePoint) {
        return new LLNop();
    }

}