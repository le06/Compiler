package edu.mit.compilers.codegen;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.compilers.codegen.ll.LLAssign;
import edu.mit.compilers.codegen.ll.LLBinaryOp;
import edu.mit.compilers.codegen.ll.LLCallout;
import edu.mit.compilers.codegen.ll.LLCmp;
import edu.mit.compilers.codegen.ll.LLExpression;
import edu.mit.compilers.codegen.ll.LLMethodCall;
import edu.mit.compilers.codegen.ll.LLMethodDecl;
import edu.mit.compilers.codegen.ll.LLNode;
import edu.mit.compilers.codegen.ll.LLReturn;
import edu.mit.compilers.codegen.ll.LLUnaryNeg;
import edu.mit.compilers.codegen.ll.LLVarLocation;

public class AddressAssign {
    private static final int BASE_OFFSET = -8;
    
    public HashMap<String, Integer> assign(ArrayList<LLNode> instructions) {
        HashMap<String, String> currentAssigments = new HashMap<String, String>();
        HashMap<String, Integer> methodMap = new HashMap<String, Integer>();
        int currentOffset = BASE_OFFSET;
        LLVarLocation currentVar;
        String currentMethod = null;
        
        for (LLNode node : instructions) {
            if (node instanceof LLMethodDecl) {
            	if (currentMethod != null) {
            		methodMap.put(currentMethod, currentAssigments.size());
            	}
            	
                // Reset map
            	currentMethod = ((LLMethodDecl)node).getName();
                currentAssigments = new HashMap<String, String>();
                currentOffset = BASE_OFFSET;
                
                for (LLVarLocation arg : ((LLMethodDecl)node).getArgs()) {
                	String addr = getAddr(currentOffset);
                    arg.setAddress(addr);
                    currentAssigments.put(arg.getLabel(), addr);
                    currentOffset += BASE_OFFSET;
                }
                
            // Deal with declarations here
            } else if (node instanceof LLAssign) {
                // If the left side is a var, not an array
                if (((LLAssign)node).getLoc() instanceof LLVarLocation) {
                    currentVar = (LLVarLocation)((LLAssign)node).getLoc();
                    // Already Assigned
                    if (currentAssigments.containsKey(currentVar.getLabel())) {
                        String addr = currentAssigments.get(currentVar.getLabel());
                        currentVar.setAddress(addr);
                    // Needs Assignment
                    } else {
                        String addr = getAddr(currentOffset);
                        currentVar.setAddress(addr);
                        currentAssigments.put(currentVar.getLabel(), addr);
                        currentOffset += BASE_OFFSET;
                    }
                }
                
                // Assign stuff to RHS, too
                if (((LLAssign)node).getExpr() instanceof LLVarLocation) {
                	LLVarLocation rhs = (LLVarLocation)((LLAssign)node).getExpr();
                	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
                } else if (((LLAssign)node).getExpr() instanceof LLBinaryOp) {
                	LLBinaryOp op = (LLBinaryOp)((LLAssign)node).getExpr();
                	
                	if (op.getLhs() instanceof LLVarLocation) {
                		LLVarLocation lhs = (LLVarLocation)op.getLhs();
                		lhs.setAddress(currentAssigments.get(lhs.getLabel()));
                	}
                	
                	if (op.getRhs() instanceof LLVarLocation) {
                		LLVarLocation rhs = (LLVarLocation)op.getRhs();
                		rhs.setAddress(currentAssigments.get(rhs.getLabel()));
                	}
                } else if (((LLAssign)node).getExpr() instanceof LLCallout) {
                	LLCallout fn = (LLCallout)((LLAssign)node).getExpr();
                	for (LLExpression a : fn.getParams()) {
                		if (a instanceof LLVarLocation) {
                			LLVarLocation rhs = (LLVarLocation)a;
                        	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
                		}
                	}
                } else if (((LLAssign)node).getExpr() instanceof LLMethodCall) {
                	LLMethodCall fn = (LLMethodCall)((LLAssign)node).getExpr();
                	for (LLExpression a : fn.getParams()) {
                		if (a instanceof LLVarLocation) {
                			LLVarLocation rhs = (LLVarLocation)a;
                        	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
                		}
                	}
                } else if (((LLAssign)node).getExpr() instanceof LLUnaryNeg) {
                	LLExpression ex = ((LLUnaryNeg)((LLAssign)node).getExpr()).getExpr();
                	LLVarLocation rhs = (LLVarLocation)ex;
                	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
                }
            } else if (node instanceof LLCallout) {
            	LLCallout fn = (LLCallout)node;
            	for (LLExpression a : fn.getParams()) {
            		if (a instanceof LLVarLocation) {
            			LLVarLocation rhs = (LLVarLocation)a;
                    	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
            		}
            	}
            } else if (node instanceof LLMethodCall) {
            	LLMethodCall fn = (LLMethodCall)node;
            	for (LLExpression a : fn.getParams()) {
            		if (a instanceof LLVarLocation) {
            			LLVarLocation rhs = (LLVarLocation)a;
                    	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
            		}
            	}
            } else if (node instanceof LLReturn) {
            	LLReturn rn = (LLReturn)node;
            	if (rn.hasReturn()) {
            		if (rn.getExpr() instanceof LLVarLocation) {
            			LLVarLocation rhs = (LLVarLocation)rn.getExpr();
                    	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
            		}
            	}
            } else if (node instanceof LLCmp) {
            	LLCmp cmp = (LLCmp)node;
            	if (cmp.getL() instanceof LLVarLocation) {
            		LLVarLocation lhs = (LLVarLocation)cmp.getL();
                	lhs.setAddress(currentAssigments.get(lhs.getLabel()));
            	}
            	
            	if (cmp.getR() instanceof LLVarLocation) {
            		LLVarLocation rhs = (LLVarLocation)cmp.getR();
                	rhs.setAddress(currentAssigments.get(rhs.getLabel()));
            	}
            }
        }
        
        if (currentMethod != null) {
    		methodMap.put(currentMethod, currentAssigments.size());
    	}
        
        return methodMap;
    }
    
    private String getAddr(int currentOffset) {
        return currentOffset + "(%rbp)";
    }
}
