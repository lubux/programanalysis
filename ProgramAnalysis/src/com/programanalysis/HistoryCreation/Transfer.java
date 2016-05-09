package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.jsnodes.Node;
import dk.brics.tajs.lattice.*;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.solver.INodeTransfer;

/**
 * Created by cedri on 5/9/2016.
 */
public class Transfer implements INodeTransfer<State, Context> {

    private NodeTransfer nodeTransfer;

    public Transfer(PointerAnalysis pointerAnalysis, HistoryCreation historyCreation){
        nodeTransfer = new NodeTransfer(pointerAnalysis, historyCreation);
    }
    @Override
    public void transfer(AbstractNode abstractNode, State state) {
        abstractNode.visitBy(this, state);
    }

    @Override
    public void transferReturn(AbstractNode abstractNode, BasicBlock basicBlock, Context context, Context contextType1, Context contextType2) {

    }

    @Override
    public void visit(Node node, State state) {
        node.visitBy(nodeTransfer, state);
    }
}
