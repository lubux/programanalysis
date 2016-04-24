package com.programanalysis.lattice;

/**
 * Created by cedri on 4/24/2016.
 */

import dk.brics.tajs.flowgraph.AbstractNode;

/** Class to represent an abstract object in the pointer analysis*/
public class AbstractObject {
    public AbstractObject(AbstractNode node){
        creationNode = node;
    }

    /** the node in the graph where this abstract object was created, could change this to NewObjectNode*/
    AbstractNode creationNode;

    public boolean equals(AbstractObject obj){
        if(creationNode.equals(obj.creationNode))
            return true;
        else
            return false;
    }
}
