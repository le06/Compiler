package edu.mit.compilers.tools;

import java.io.PrintStream;
import java.util.Stack;

import edu.mit.compilers.codegen.ll.*;

public class LLVisualize implements LLNodeVisitor{
    Stack<String> parents = new Stack<String>();
    
    int currentSuffix = 0;
    String BASE = "N";
    
    PrintStream output;
    
    String name; // Temp used in most visitors
    
    public String getNextNode() {
        return BASE + currentSuffix++;
    }
    
    public String getConnector(String node1, String node2) {
        return node1  + " -> " + node2;
    }
    
    public void writeNodeInfo(String name, String description) {
        output.println(getConnector(parents.peek(), name));
        output.println(name + " [label=\"" + description + "\"]");
    }
    
    public void visualize(LLFile tree, PrintStream out) {
        output = out;        
        output.println("digraph LL {\n");

        tree.accept(this);
        
        output.println("}");
    }

    @Override
    public void visit(LLFile node) {
        parents.push(getNextNode());
        
        for (LLArrayDecl a : node.getArrayDecls()) {
            a.accept(this);
        }
        
        for (LLMethodDecl m : node.getMethods()) {
            m.accept(this);
        }
        
        node.getMain().accept(this);
        
        parents.pop();
    }   

    @Override
    public void visit(LLGlobalDecl node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getLabel().getName());
        
        parents.push(name);
        node.getMalloc().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLArrayDecl node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getLabel().getName());
        
        parents.push(name);
        node.getMalloc().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLMalloc node) {
        writeNodeInfo(getNextNode(), Long.toString(node.getSize()));
    }

    @Override
    public void visit(LLMethodDecl node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getName());
        
        parents.push(name);
        node.getEnv().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLEnvironment node) {
        name = getNextNode();
        
        writeNodeInfo(name, "ENV");
        
        parents.push(name);
        for (LLNode n : node.getSubnodes()) {
            n.accept(this);
        }
        parents.pop();
    }

    @Override
    public void visit(LLAssign node) {
        name = getNextNode();
        
        writeNodeInfo(name, "ASSIGN");
        
        parents.push(name);
        node.getLoc().accept(this);
        node.getExpr().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLMethodCall node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getMethodName());
        
        parents.push(name);
        for (LLExpression l : node.getParams()) {
            l.accept(this);
        }
        parents.pop();
    }

    @Override
    public void visit(LLCallout node) {
        name = getNextNode();
        
        writeNodeInfo(name, "Callout: " + node.getFnName());
        
        parents.push(name);
        for (LLExpression l : node.getParams()) {
            l.accept(this);
        }
        parents.pop();
    }

    @Override
    public void visit(LLStringLiteral node) {
        name = getNextNode();
        //String x = node.getText().substring(1, node.getText().length() - 2);
        //writeNodeInfo(name, node.getText().replaceAll("\"", "\\\""));
        writeNodeInfo(name, "STRING");
    }

    @Override
    public void visit(LLVarLocation node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getLabel());
    }

    @Override
    public void visit(LLArrayLocation node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getLabel());
        
        parents.push(name);
        node.getIndexExpr().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLBinaryOp node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getOp().toString());
        
        parents.push(name);
        node.getLhs().accept(this);
        node.getRhs().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLUnaryNeg node) {
        name = getNextNode();
        
        writeNodeInfo(name, "-");
        
        parents.push(name);
        node.getExpr().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLUnaryNot node) {
        name = getNextNode();
        
        writeNodeInfo(name, "NOT");
        
        parents.push(name);
        node.getExpr().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLBoolLiteral node) {
        name = getNextNode();
        
        writeNodeInfo(name, Boolean.toString(node.getValue()));
    }

    @Override
    public void visit(LLIntLiteral node) {
        name = getNextNode();
        
        writeNodeInfo(name, Long.toString(node.getValue()));
    }

    @Override
    public void visit(LLJump node) {
        name = getNextNode();
        
        writeNodeInfo(name, "JMP " + node.getLabel().getName());
    }

    @Override
    public void visit(LLLabel node) {
        name = getNextNode();
        
        writeNodeInfo(name, node.getName());
    }

    @Override
    public void visit(LLMov node) {
        name = getNextNode();
        
        writeNodeInfo(name, "MOV " + node.getSrc() + " " + node.getDest());
    }

    @Override
    public void visit(LLReturn node) {
        name = getNextNode();
        
        writeNodeInfo(name, "RET");
        
        parents.push(name);
        node.getExpr().accept(this);
        parents.pop();
    }

    @Override
    public void visit(LLNop node) {
        name = getNextNode();
        
        writeNodeInfo(name, "NOP");
    }

}
