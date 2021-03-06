package com.programanalysis.PointerAnalysis;

import java.util.*;

/**
 * Created by cedri on 4/28/2016.
 * used for the entry of each function to store the set of objects that 'this' points to
 * and the set of objects that each function argument points to
 */
public class InState {
    public InState(){
        thisObjects = new HashSet<AbstractObject>();
        argumentObjects = new HashMap<String,Set<AbstractObject>>();
    }

    public Set<AbstractObject> thisObjects;

    public Map<String,Set<AbstractObject>> argumentObjects;
}
