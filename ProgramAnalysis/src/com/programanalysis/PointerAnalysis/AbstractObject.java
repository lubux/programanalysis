package com.programanalysis.PointerAnalysis;

/**
 * Created by cedri on 4/24/2016.
 */

import dk.brics.tajs.flowgraph.AbstractNode;

/** Class to represent an abstract object in the pointer analysis*/
public class AbstractObject {
    public AbstractObject(AbstractNode node){
        creationNode = node;
        stringvalue = null;
        ID = null;
    }

    /* used for abstract objects that are put in to not called function arguments or global object like document, console, window etc. ...*/
    public AbstractObject(String id){
        creationNode = null;
        stringvalue = null;
        ID = id;
    }

    public AbstractObject(AbstractNode node, String string){
        creationNode = node;
        stringvalue = string;
        ID = null;
    }


    public String getStringValue(){
        return stringvalue;
    }
    /** the node in the graph where this abstract object was created, could change this to NewObjectNode*/
    AbstractNode creationNode;

    /** string value used in the case of a property access a[s]*/
    String stringvalue;

    /** used for abstract objects that are not created in call nodes, (document, console, function arguments of not called functions*/
    String ID;

    public boolean equals(Object obj){
        if(! (obj instanceof AbstractObject)){
            return false;
        }
        AbstractObject other = (AbstractObject) obj;

        if(ID != null){
            if(other.ID != null){
                return ID.equals(other.ID);
            } else {
                // other doesn't have an ID and this does, return false
                return false;
            }
        } else{
            if(other.ID != null){
                // other has an ID but this doesn't, return false
                return false;
            }
        }

        // TODO: is this safe?
        // compare the string values because strings aren't abstract objects anyway*/
        /*if(obj instanceof AbstractObject && ((AbstractObject)obj).stringvalue != null & stringvalue != null){
            return ((AbstractObject)obj).stringvalue.equals(stringvalue);
        }*/
        return (other.creationNode.equals(creationNode));
    }

    public int hashCode(){
        if(creationNode != null) {
            return creationNode.hashCode();
        } else {
            return ID.hashCode();
        }
    }

}
