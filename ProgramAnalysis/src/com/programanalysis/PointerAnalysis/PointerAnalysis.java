package com.programanalysis.PointerAnalysis;

import com.programanalysis.util.QueueEntry;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.solver.CallGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by cedri on 4/28/2016.
 */
public class PointerAnalysis {
    public PointerAnalysis(Analysis analysis){
        this.analysis = analysis;
    }

    private GlobalState state;

    private Transfer visitor;

    private Analysis analysis;

    public static FlowGraph flowgraph;

    public static CallGraph callgraph;

    private Map<BasicBlock, BlockRegisters> blockRegisters;

    private PriorityQueue<QueueEntry> worklist;

    public void init(){
        state = new GlobalState();
        flowgraph = analysis.getSolver().getFlowGraph();
        callgraph = analysis.getSolver().getAnalysisLatticeElement().getCallGraph();
        blockRegisters = new HashMap<BasicBlock, BlockRegisters>();
        worklist = new PriorityQueue<QueueEntry>();
        worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
        visitor = new Transfer(this);
    }

    public GlobalState getState(){
        return state;
    }

    public BlockRegisters getRegisters(BasicBlock block){
        if(! blockRegisters.containsKey(block)){
            BlockRegisters b = new BlockRegisters();
            blockRegisters.put(block,b);
        }
        return blockRegisters.get(block);
    }

    public void solve(){
        while(! worklist.isEmpty()){
            QueueEntry entry = worklist.remove();
            BasicBlock block = entry.getBlock();
            // go through the nodes of the basic block
            for(AbstractNode node: block.getNodes()){
                visitor.transfer(node, null);
            }
            // add the block successors to the worklist
            for (Iterator<BasicBlock> i = block.getSuccessors().iterator(); i.hasNext(); ) {
                worklist.add(new QueueEntry(i.next()));
            }
        }
        //todo: add program entry again to the worklist if the store changed or some function state changed since the last flow through
    }
}
