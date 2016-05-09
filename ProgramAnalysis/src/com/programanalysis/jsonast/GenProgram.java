package com.programanalysis.jsonast;

import dk.brics.tajs.flowgraph.SourceLocation;

import java.util.Map;

/**
 * Created by lukas on 09.05.16.
 * Represents a program generated from the JSONAst
 */
public class GenProgram {

    private String name;

    private String code;

    private Map<SourceLocation, Integer> sourceToID;

    GenProgram(String name, String code, Map<SourceLocation, Integer> sourcetoID) {
        this.name = name;
        this.code = code;
        this.sourceToID = sourcetoID;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getIdForSourceLocation(SourceLocation loc) {
        return sourceToID.get(loc);
    }

    public Map<SourceLocation, Integer> getSourceToID() {
        return sourceToID;
    }

    @Override
    public String toString() {
        return name + ":\n" + code + " -> " + sourceToID.toString();
    }
}
