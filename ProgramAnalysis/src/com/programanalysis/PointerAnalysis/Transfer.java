package com.programanalysis.PointerAnalysis;

import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.jsnodes.*;
import dk.brics.tajs.lattice.Context;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.solver.IContext;
import dk.brics.tajs.solver.INodeTransfer;
import dk.brics.tajs.solver.IState;

/**
 * Created by cedri on 4/28/2016.
 */
public class Transfer implements INodeTransfer<State, Context>{

    NodeTransfer nodeTransfer;

    PointerAnalysis analysis;

    public Transfer(PointerAnalysis analysis){
        this.analysis = analysis;
        nodeTransfer = new NodeTransfer(analysis);
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
