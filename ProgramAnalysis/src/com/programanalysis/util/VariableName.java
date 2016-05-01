package com.programanalysis.util;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.flowgraph.Function;

/**
 * Created by cedri on 4/27/2016.
 */
public class VariableName {
    /** returns the full name of a variable that is used for the store (i.e. all outter function
     * names and the variable identifier
     */
    public static String getVariableName(Function scope, String identifier){
        String sol = "";
        while(! scope.equals(PointerAnalysis.flowgraph.getMain())){
            sol = scope.getName() + "." + sol;
            scope = scope.getOuterFunction();
        }
        return sol + "." + identifier;
    }

}
