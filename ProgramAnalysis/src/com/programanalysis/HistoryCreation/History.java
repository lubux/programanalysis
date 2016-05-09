package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cedri on 5/9/2016.
 */
public class History {

    public History(AbstractObject obj){
        this.historySet = new HashSet<List<String>>();
        this.abstractObject = obj;
    }

    /** adds all the histories of other to the set of this abtract object histories*/
    public void merge(History other){
        for(List<String> l: other.historySet){
            this.historySet.add(new ArrayList<String>(l));
        }
    }

    /** adds apiCall to every history of this object*/
    public void add(String apiCall){
        for(List<String> l: historySet){
            l.add(apiCall);
        }
    }

    public AbstractObject getAbstractObject(){
        return abstractObject;
    }

    private AbstractObject abstractObject;

    private Set<List<String>> historySet;

    public boolean equals(Object obj){
        if(obj instanceof History){
            History other = (History) obj;
            return this.abstractObject.equals(other.abstractObject) && other.historySet.equals(this.historySet);
        } else {
            return false;
        }
    }

    /** returns a copy of this history, the lists in historyset are new objects*/
    public History copy(){
        History res = new History(abstractObject);
        // copy all the history lists to res
        for(List<String> l: historySet){
            res.historySet.add(new ArrayList<String>(l));
        }
        return res;
    }
}
