package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import dk.brics.tajs.flowgraph.AbstractNode;

/**
 * Created by cedri on 5/12/2016.
 */
public class APICallTuple {
    private AbstractNode node;

    private String string;

    public APICallTuple(AbstractNode node, String string){
        this.node = node;
        this.string = string;
    }

    public String getString(){
        return string;
    }

    public AbstractNode getNode(){
        return node;
    }

    public boolean equals(Object other){
        if(! (other instanceof  APICallTuple)){
            return false;
        }
        APICallTuple otherTuple = (APICallTuple) other;
        return string.equals((otherTuple).getString());
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

}
