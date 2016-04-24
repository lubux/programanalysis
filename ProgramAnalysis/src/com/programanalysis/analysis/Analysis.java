package com.programanalysis.analysis;

import com.programanalysis.lattice.CallEdge;
import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import com.programanalysis.monitoring.iMonitoring;
import dk.brics.tajs.analysis.*;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.options.Options;
import dk.brics.tajs.solver.*;


/**
 * Created by cedri on 4/22/2016.
 */




public class Analysis implements IAnalysis<State, Context, CallEdge, iMonitoring, Analysis> {

    private final Solver solver;

    private final InitialStateBuilder initial_state_builder;

    private final Transfer transfer;

    private final WorkListStrategy worklist_strategy;

    private final iMonitoring monitoring;

    private final EvalCache eval_cache;

    private final IContextSensitivityStrategy context_sensitivity_strategy;


    public Analysis(iMonitoring monitoring, SolverSynchronizer sync){
        this.monitoring = monitoring;
        initial_state_builder = new InitialStateBuilder();
        transfer = new Transfer();
        worklist_strategy = new WorkListStrategy();
        eval_cache = new EvalCache();
        if (Options.get().isDeterminacyEnabled()) {
            context_sensitivity_strategy = new StaticDeterminacyContextSensitivityStrategy(StaticDeterminacyContextSensitivityStrategy.SyntacticHints.get());
        } else {
            context_sensitivity_strategy = new BasicContextSensitivityStrategy();
        }
        solver = new Solver(this, sync);
    }


    @Override
    public IAnalysisLatticeElement makeAnalysisLattice(FlowGraph flowGraph) {
        return null;
    }

    @Override
    public IInitialStateBuilder getInitialStateBuilder() {
        return initial_state_builder;
    }

    @Override
    public Transfer getNodeTransferFunctions() {
        return transfer;
    }

    @Override
    public Transfer getEdgeTransferFunctions() {
        return transfer;
    }

    @Override
    public IWorkListStrategy<Context> getWorklistStrategy() {
        return worklist_strategy;
    }

    @Override
    public iMonitoring getMonitoring() {
        return monitoring;
    }

    @Override
    public void setSolverInterface(GenericSolver.SolverInterface solverInterface) {

    }

    @Override
    public CallEdge makeCallEdge(State state) {
        return null;
    }

}
