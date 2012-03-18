package edu.mit.compilers.codegen.ll;

import edu.mit.compilers.checker.Ir.IrBinOperator;

public class llBinOp implements llExpression {
    llExpression l;
    llExpression r;
    IrBinOperator op;
    
    public llBinOp(llExpression lhs, llExpression rhs, IrBinOperator binop) {
        l = lhs;
        r = rhs;
        op  = binop;
    }

    @Override
    public void accept(llNodeVisitor v) {
        l.accept(v);
        r.accept(v);
        v.visit(this);
    }

}
