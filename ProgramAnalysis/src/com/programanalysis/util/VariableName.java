package com.programanalysis.util;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.lattice.State;
import dk.brics.tajs.flowgraph.AbstractNode;
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

    public static String getVariableName(Function scope, String identifier, State state){
        String sol = "";
        while(! scope.equals(state.getSolverInterface().getFlowGraph().getMain())){
            sol = scope.getName() + "." + sol;
            scope = scope.getOuterFunction();
        }
        return sol + "." + identifier;
    }
}
