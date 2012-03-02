package edu.mit.compilers.tools;

import antlr.collections.*;

public class TreeVisualizer {
    public TreeVisualizer() {
        
    }
    
    public static String generateDOT(AST ast) {
        StringBuilder out = new StringBuilder();
        out.append("digraph AST {\n");
        
        walk(ast, out, "a", (char)('a'-1));
        
        out.append("}");
        
        return out.toString();
    }
    
    private static void walk(AST ast, StringBuilder out, String parent, char last) {
        if (ast == null) {
            return;
        }
        
        out.append((parent + (char)(last + 1)) + " [label=\"" + ast.getText() + "\"]\n");
        
        out.append(parent + " -> " + (parent + (char)(last + 1)) // Link
                
                + "\n");
        
        if (ast.getFirstChild() != null) {
            walk(ast.getFirstChild(), out, parent + (char)(last + 1), (char)('a'-1));
        } else {
            walk(ast.getNextSibling(), out, parent, (char)(last+1));
        }
        
    }
}
