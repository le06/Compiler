package edu.mit.compilers.checker.Ir;

import edu.mit.compilers.checker.SemanticChecker;

public interface IrExpression extends IrNode {
	// note the argument: type lookup is based on context.
	// specifically, this is necessary to determine the type of a location.
    IrType getExprType(SemanticChecker v);
    
    String toString(int i);
}