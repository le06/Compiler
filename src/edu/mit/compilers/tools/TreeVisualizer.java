package edu.mit.compilers.tools;

import antlr.collections.*;

public class TreeVisualizer {
    public TreeVisualizer() {
        
    }
    
    public static String generateDOT(AST ast) {
        StringBuilder out = new StringBuilder();
        out.append("digraph AST {\n");
        
        walk(ast, out, "a", 'a');
        
        out.append("}");
        
        return out.toString();
    }
    
    private static void walk(AST ast, StringBuilder out, String parent, char name) {
        if (ast == null) {
            return;
        }
        
        out.append((parent + name) + " [label=\"" + ast.getText() + "\"]\n");
        
        out.append(parent + " -> " + (parent + name) // Link
                
                + "\n");
        
        AST child = ast.getFirstChild();
        
        
        char next = 'a';
        for (int i = 0; i < ast.getNumberOfChildren(); i++) {
            walk(child, out, parent + name, next++);
            child = child.getNextSibling();
        }
        
        /*if (ast.getFirstChild() != null) {
            walk(ast.getFirstChild(), out, parent + (char)(last + 1), (char)('a'-1));
        }//
        //else {
        for (int i = 0; i < ast.getN)
            walk(ast.getNextSibling(), out, parent, (char)(last+1));
        //}
*/        
    }
    

}
