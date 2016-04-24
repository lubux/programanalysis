package com.programanalysis.analysis;

import com.programanalysis.lattice.Context;
import com.programanalysis.lattice.State;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.jsnodes.Node;
import dk.brics.tajs.solver.IEdgeTransfer;
import dk.brics.tajs.solver.INodeTransfer;

/**
 * Created by cedri on 4/24/2016.
 */
public class Transfer implements
        INodeTransfer<State, Context>,
        IEdgeTransfer<State, Context> {

    private NodeTransfer nodetransfer;

    private EdgeTransfer edgetransfer;
    @Override
    public void transfer(AbstractNode abstractNode, State state) {

    }

    @Override
    public void transferReturn(AbstractNode abstractNode, BasicBlock basicBlock, Context context, Context contextType1, Context contextType2) {

    }

    @Override
    public void visit(Node node, State state) {

    }

    @Override
    public Context transfer(BasicBlock basicBlock, BasicBlock basicBlock1, State state) {
        return null;
    }
}
