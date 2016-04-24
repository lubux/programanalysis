package com.programanalysis.analysis;

import com.programanalysis.lattice.CallEdge;
import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import com.programanalysis.monitoring.iMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.solver.SolverSynchronizer;

/**
 * Created by cedri on 4/24/2016.
 */
public class Solver extends GenericSolver<State, Context, CallEdge, iMonitoring, Analysis> {
    public Solver(Analysis analysis, SolverSynchronizer solverSynchronizer) {
        super(analysis, solverSynchronizer);
    }
}
