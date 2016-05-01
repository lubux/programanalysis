package com.programanalysis.lattice;

/**
 * Created by cedri on 4/24/2016.
 */

import dk.brics.tajs.flowgraph.AbstractNode;

/** Class to represent an abstract object in the pointer analysis*/
public class AbstractObject {
    public AbstractObject(AbstractNode node){
        creationNode = node;
        stringvalue = null;
    }

    public AbstractObject(AbstractNode node, String string){
        creationNode = node;
        stringvalue = string;
    }


    public String getStringValue(){
        return stringvalue;
    }
    /** the node in the graph where this abstract object was created, could change this to NewObjectNode*/
    AbstractNode creationNode;

    /** string value used in the case of a property access a[s]*/
    String stringvalue;

    public boolean equals(Object obj){
        // compare the string values because strings aren't abstract objects anyway*/
        if(obj instanceof AbstractObject && ((AbstractObject)obj).stringvalue != null & stringvalue != null){
            return ((AbstractObject)obj).stringvalue.equals(stringvalue);
        }
        return (obj instanceof AbstractObject) && ((AbstractObject)obj).creationNode.equals(creationNode);
    }

    public int hashCode(){
        return creationNode.hashCode();
    }

}
