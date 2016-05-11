package com.programanalysis.PointerAnalysis;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cedri on 4/28/2016.
 * used for the return point of each function to store the set of abstract objects that the return value points to
 * makes no sense to use this for constructors
 */
public class OutState {
    public OutState(){
        returnObjects = new HashSet<AbstractObject>();
    }

    public Set<AbstractObject> returnObjects;


}
