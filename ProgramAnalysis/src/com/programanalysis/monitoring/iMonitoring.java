package com.programanalysis.monitoring;

import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.solver.ISolverMonitoring;

import java.util.Collection;

/**
 * Created by cedri on 4/24/2016.
 */
public interface iMonitoring extends ISolverMonitoring<State, Context> {

}
