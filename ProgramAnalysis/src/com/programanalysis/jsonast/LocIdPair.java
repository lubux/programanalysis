package com.programanalysis.jsonast;

import dk.brics.tajs.flowgraph.SourceLocation;
import net.htmlparser.jericho.Source;

/**
 * Created by lukas on 10.05.16.
 */
public class LocIdPair {

    private SourceLocation location;
    private int id;

    LocIdPair(SourceLocation location, int id) {
        this.location = location;
        this.id = id;
    }

    public SourceLocation getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ID:"+id+"->"+location;
    }
}
