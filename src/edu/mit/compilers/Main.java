package edu.mit.compilers;

import java.io.*;

import antlr.CommonAST;
import antlr.Token;
import antlr.collections.AST;
import edu.mit.compilers.checker.DecafChecker;
import edu.mit.compilers.codegen.DecafUnoptimizedCodeGenerator;
import edu.mit.compilers.grammar.*;
import edu.mit.compilers.tools.CLI;
import edu.mit.compilers.tools.CLI.Action;
import edu.mit.compilers.tools.LLVisualize;
//import edu.mit.compilers.tools.GenericTreeWalk;
import edu.mit.compilers.tools.TreeVisualizer;

class Main {
  public static void main(String[] args) {
    try {
      CLI.parse(args, new String[0]);
      InputStream inputStream = args.length == 0 ?
          System.in : new java.io.FileInputStream(CLI.infile);

      if (CLI.target == Action.SCAN) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        scanner.setTrace(CLI.debug);
        Token token;
        boolean done = false;
        while (!done) {
          try {
            for (token = scanner.nextToken();
                 token.getType() != DecafParserTokenTypes.EOF;
                 token = scanner.nextToken()) {
              String type = "";
              String text = token.getText();
              switch (token.getType()) {
               case DecafScannerTokenTypes.ID:
                type = " IDENTIFIER";
                break;
               case DecafScannerTokenTypes.STRING:
                type = " STRINGLITERAL";
                break;
               case DecafScannerTokenTypes.DEC_LITERAL:
               case DecafScannerTokenTypes.HEX_LITERAL:
               case DecafScannerTokenTypes.BIN_LITERAL:
                type = " INTLITERAL";
                break; 
               case DecafScannerTokenTypes.CHAR:
                type = " CHARLITERAL";
                break;
               case DecafScannerTokenTypes.TK_true:
               case DecafScannerTokenTypes.TK_false:
                type = " BOOLEANLITERAL";
                break;
               default:
//                System.out.println(" OTHER:"  + token.getType());
                break;
              }
              System.out.println(token.getLine() + type + " " + text);
            }
            done = true;
          } catch(Exception e) {
            // print the error:
            System.out.println(CLI.infile + " " + e);
            scanner.consume();
          }
        }
      } else if (CLI.target == Action.PARSE ||
                 CLI.target == Action.DOT ||
                 CLI.target == Action.DEFAULT) {
        DecafScanner scanner =
            new DecafScanner(new DataInputStream(inputStream));
        DecafParser parser = new DecafParser(scanner);
        parser.setTrace(CLI.debug);
        parser.program();
        if (parser.getError()) {
          System.exit(-1);
        }
        
        if (CLI.target == Action.DOT) {
            System.out.println(TreeVisualizer.generateDOT(parser.getAST()));
        }
      } else if (CLI.target == Action.INTER) {
        DecafScanner scanner = 
            new DecafScanner(new DataInputStream(inputStream));
        DecafParser parser = new DecafParser(scanner);
        DecafChecker checker = new DecafChecker(parser);
        checker.setTrace(CLI.debug);
        checker.check();
        if (checker.getError()) {
            System.exit(-1);
        }
        
      } else if (CLI.target == Action.ASSEMBLY ||
                 CLI.target == Action.CFG) {
          DecafScanner scanner = 
                  new DecafScanner(new DataInputStream(inputStream));
          DecafParser parser = new DecafParser(scanner);
          DecafChecker checker = new DecafChecker(parser);
          DecafUnoptimizedCodeGenerator gen = new DecafUnoptimizedCodeGenerator(checker);
          gen.setTrace(CLI.debug);
         
 //         System.out.println(CLI.outfile);
          File f = new File(CLI.outfile);
          
          f.createNewFile();
          
          BufferedWriter out = new BufferedWriter(
                                    new FileWriter(f));
          
          gen.gen(out);
          out.close();
          if (gen.getError()) {
              System.exit(-1);
          }
          
          if (CLI.target == Action.CFG) {
              LLVisualize v = new LLVisualize();
              v.visualize(gen.getLLRep(), System.out);
          }
      }
    } catch(Exception e) {
      // print the error:
      System.out.println(CLI.infile+" "+e);
    }
  }
}
