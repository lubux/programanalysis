package com.programanalysis.analysis;

import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.solver.IEdgeTransfer;

/**
 * Created by cedri on 4/24/2016.
 */
public class EdgeTransfer implements IEdgeTransfer<State, Context> {
    @Override
    public Context transfer(BasicBlock basicBlock, BasicBlock basicBlock1, State state) {
        return null;
    }
}
