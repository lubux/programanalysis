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

    private int maxLength = 25;

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
        if(apiCall == null){
            return;
        }
        if(historySet.isEmpty()){
            historySet.add(new ArrayList<String>());
        }
        for(List<String> l: historySet){
            if(l.size()< maxLength){
                l.add(apiCall);
            }
        }
    }

    /** adds every history in otherHistory to every history in historySet*/
    public void add(History otherHistory){
        Set<List<String>> newHistorySet = new HashSet<List<String>>();
        for(List<String> histList: otherHistory.historySet){
            if(historySet.isEmpty()){
                historySet.add(new ArrayList<String>());
            }
            for(List<String> thisList: historySet) {
                List<String> l = new ArrayList<String>();
                l.addAll(thisList);
                for(String s: histList){
                    if(l.size() < maxLength){
                        l.add(s);
                    }
                }
                newHistorySet.add(l);
            }
        }
        historySet = newHistorySet;
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

    public String print(){
        String res = "";
        for (List<String> l: historySet){
            String partRes = "";
            for(int i = 0; i < l.size(); i++){
                if(l.get(i) != null) {
                    partRes = partRes + l.get(i);
                    if (i != l.size() - 1) {
                        partRes = partRes + " ";
                    }
                }
            }
            if(!partRes.equals("")) {
                partRes = partRes + "\n";
            }
            res = res + partRes;
        }
        return res;
    }
}
