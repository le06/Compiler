package edu.mit.compilers.optimizer;

import edu.mit.compilers.codegen.BasicBlock;
import java.util.ArrayList;

public class DecafOptimizer {
	ArrayList<Optimization> optimizations;
	
	public DecafOptimizer() {
		optimizations = new ArrayList<Optimization>();
	}
	
	public void optimize(BasicBlock methods) {
		for (BasicBlock method : methods.getChildren()) {
			for (Optimization optimization : optimizations) {
				optimization.optimize(method);
			}
		}
	}
}
