package edu.mit.compilers.checker.Ir;

public class IrArrayDecl extends IrFieldDecl {
    private IrType type;
    private IrIdentifier id;
    private IrIntLiteral array_size;
}