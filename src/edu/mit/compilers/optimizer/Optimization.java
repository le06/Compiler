package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;

public interface Optimization {
	public void optimize(BasicBlock method);
}
