package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import dk.brics.tajs.flowgraph.AbstractNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by cedri on 5/9/2016.
 */
public class History {

    private int maxCount = 32;

    private int maxLength = 64;

    public History(AbstractObject obj){
        this.historySet = new HashSet<List<APICallTuple>>();
        historySet.add(new ArrayList<APICallTuple>());
        this.abstractObject = obj;
    }

    /** second constructor that adds no empty list, used for the copy function*/
    public History(AbstractObject obj, boolean copy){
        this.historySet = new HashSet<List<APICallTuple>>();
        this.abstractObject = obj;
    }

    /** adds all the histories of other to the set of this abtract object histories*/
    public void merge(History other){
        for(List<APICallTuple> l: other.historySet){
            if(historySet.size() < maxCount) {
                this.historySet.add(new ArrayList<APICallTuple>(l));
            }
        }
    }

    /** adds apiCall to every history of this object*/
    public void add(APICallTuple apiCall){
        if(apiCall == null){
            return;
        }
        if(historySet.isEmpty()){
            historySet.add(new ArrayList<APICallTuple>());
        }
        for(List<APICallTuple> l: historySet){
            if(l.size()< maxLength){
                l.add(apiCall);
            }
        }
    }

    /** adds every history in otherHistory to every history in historySet*/
    public void add(History otherHistory){
        Set<List<APICallTuple>> newHistorySet = new HashSet<List<APICallTuple>>();
        for(List<APICallTuple> histList: otherHistory.historySet){
            if(historySet.isEmpty()){
                historySet.add(new ArrayList<APICallTuple>());
            }
            for(List<APICallTuple> thisList: historySet) {
                List<APICallTuple> l = new ArrayList<APICallTuple>();
                l.addAll(thisList);
                for(APICallTuple s: histList){
                    if(l.size() < maxLength){
                        l.add(s);
                    }
                }
                if(newHistorySet.size() < maxCount) {
                    newHistorySet.add(l);
                }
            }
        }
        historySet = newHistorySet;
    }

    public AbstractObject getAbstractObject(){
        return abstractObject;
    }

    private AbstractObject abstractObject;

    private Set<List<APICallTuple>> historySet;

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
        History res = new History(abstractObject, true);
        // copy all the history lists to res
        for(List<APICallTuple> l: historySet){
            res.historySet.add(new ArrayList<APICallTuple>(l));
        }
        return res;
    }

    public String print(){
        String res = "";
        if(historySet.size() <= maxCount) {
            for (List<APICallTuple> l : historySet) {
                String partRes = "";
                for (int i = 0; i < l.size(); i++) {
                    if (l.get(i) != null) {
                        partRes = partRes + "[" + l.get(i).getString() + "]";
                        if (i != l.size() - 1) {
                            partRes = partRes + " ";
                        }
                    }
                }
                if (!partRes.equals("")) {
                    partRes = partRes + "\n";
                }
                res = res + partRes;
            }
        } else {
            // we only print random maxCount histories
            Set<Integer> s = new HashSet<Integer>();
            while(s.size() < maxCount){
                s.add(ThreadLocalRandom.current().nextInt(0, historySet.size()));
            }
            int index = 0;
            for(Iterator<List<APICallTuple>> i = historySet.iterator(); i.hasNext();index++){
                List<APICallTuple> l = i.next();
                if(s.contains(new Integer(index))){
                    String partRes = "";
                    for (int j = 0; j < l.size(); j++) {
                        if (l.get(j) != null) {
                            partRes = partRes + "[" + l.get(j).getString() + "]";
                            if (j != l.size() - 1) {
                                partRes = partRes + " ";
                            }
                        }
                    }
                    if (!partRes.equals("")) {
                        partRes = partRes + "\n";
                    }
                    res = res + partRes;
                }
            }
        }
        return res;
    }

    /** contains only histories with a '?' and cuts the rest after the '?'*/
    public String printPredictionHistory(){
        String res = "";
        for(List<APICallTuple> l: historySet){
            String partRes = "";
            for(int j = 0; j < l.size(); j++){
                if(l.get(j).getString().equals("?")){
                    partRes = partRes + "<?>";
                    res = res + partRes + "\n";
                    break;
                } else {
                    partRes = partRes + "[" + l.get(j).getString() + "]";
                    if (j != l.size() - 1) {
                        partRes = partRes + " ";
                    }
                }
            }
        }
        return res;
    }

    /** puts the progId and nodeId at the start of each line*/
    public String printExtractionHistory(int progId, int nodeId){
        String res = "";
        for (List<APICallTuple> l : historySet) {
            String partRes = progId + " " + nodeId + " ";
            for (int i = 0; i < l.size(); i++) {
                if (l.get(i) != null) {
                    partRes = partRes + "[" + l.get(i).getString() + "]";
                    if (i != l.size() - 1) {
                        partRes = partRes + " ";
                    }
                }
            }
            if (!partRes.equals("")) {
                partRes = partRes + "\n";
            }
            res = res + partRes;
        }
        return res;
    }
}
