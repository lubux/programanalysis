package com.programanalysis.monitoring;

import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;

import java.util.Collection;

/**
 * Created by cedri on 4/24/2016.
 */
public class Monitoring implements iMonitoring {
    @Override
    public void visitNodeTransfer(AbstractNode abstractNode) {

    }

    @Override
    public void visitBlockTransfer(BasicBlock basicBlock, State state) {

    }

    @Override
    public void visitPostBlockTransfer(BasicBlock basicBlock, State state) {

    }

    @Override
    public void visitNewFlow(BasicBlock basicBlock, Context context, State state, String s, String s1) {

    }

    @Override
    public void visitUnknownValueResolve(boolean b, boolean b1) {

    }

    @Override
    public void visitRecoveryGraph(int i) {

    }

    @Override
    public void visitFunction(Function function, Collection<State> collection) {

    }

    @Override
    public void visitReachableNode(AbstractNode abstractNode) {

    }

    @Override
    public void visitJoin() {

    }

    @Override
    public boolean allowNextIteration() {
        return false;
    }
}
