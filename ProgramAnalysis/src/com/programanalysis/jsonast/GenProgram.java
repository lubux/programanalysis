package com.programanalysis.jsonast;

import dk.brics.tajs.flowgraph.SourceLocation;

import java.util.*;

/**
 * Created by lukas on 09.05.16.
 * Represents a program generated from the JSON-Ast
 */
public class GenProgram {

    private int id;

    private String name;

    private String code;

    private Map<SourceLocation,Set<Integer>> sourceToIDs;

    private SourceLocation[] sourceLocations;

    private Set<Integer> markedNodes = new HashSet<>();

    GenProgram(int id, String name, String code, List<LocIdPair> sourcetoID) {
        this.id = id;
        this.name = name;
        this.code = code;
        initDatastructures(sourcetoID);
    }

    private void initDatastructures(List<LocIdPair> sourceToID) {
        this.sourceToIDs = new HashMap<>();
        this.sourceLocations = new SourceLocation[sourceToID.size()];
        for (LocIdPair entry : sourceToID) {
            this.sourceLocations[entry.getId()] = entry.getLocation();
            if(!sourceToIDs.containsKey(entry.getLocation())) {
                sourceToIDs.put(entry.getLocation(), new HashSet<>());
            }
            Set<Integer> ids = sourceToIDs.get(entry.getLocation());
            ids.add(entry.getId());
        }

    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void markNode(int id) {
        markedNodes.add(id);
    }

    public Iterator<Integer> getMarkedNodesIterator() {
        return markedNodes.iterator();
    }

    public int getId() {
        return id;
    }

    public int[] getIdsForSourceLocation(SourceLocation loc) {
        Set<Integer> ids =  sourceToIDs.get(loc);
        int[] res = new int[ids.size()];
        int idx = 0;
        for (Integer val : ids) {
            res[idx] = val;
            idx++;
        }
        return res;
    }

    public SourceLocation getSourceLocationForID(int id) {
        if(id<0 || id>=sourceLocations.length)
            throw new IllegalArgumentException("ID  not valid " + id);
        return this.sourceLocations[id];
    }


    /**
     * !Aliasing
     * @return
     */
    public SourceLocation[] getSourceLocations() {
        return sourceLocations;
    }

    @Override
    public String toString() {
        return name + ":\n" + code + " -> " + sourceToIDs.toString();
    }
}
