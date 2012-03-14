package edu.mit.compilers.codegen.ll;

import java.util.ArrayList;

public class llFile  {
    llEnvironment main;
    ArrayList<llEnvironment> methods;
    
    public llFile(llEnvironment mainMethod) {
        main = mainMethod;
    }
}
