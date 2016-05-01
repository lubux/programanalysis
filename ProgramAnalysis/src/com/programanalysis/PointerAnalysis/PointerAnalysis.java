package com.programanalysis.PointerAnalysis;

import com.programanalysis.util.QueueEntry;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.flowgraph.Function;
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

    private Map<Function, BlockRegisters> blockRegisters;

    private PriorityQueue<QueueEntry> worklist;

    public void init(){
        state = new GlobalState();
        flowgraph = analysis.getSolver().getFlowGraph();
        callgraph = analysis.getSolver().getAnalysisLatticeElement().getCallGraph();
        blockRegisters = new HashMap<Function, BlockRegisters>();
        worklist = new PriorityQueue<QueueEntry>();
        worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
        visitor = new Transfer(this);
    }

    public GlobalState getState(){
        return state;
    }

    public CallGraph getCallGraph(){
        return callgraph;
    }

    public FlowGraph getFlowgraph(){
        return flowgraph;
    }

    public BlockRegisters getRegisters(BasicBlock block){
        if(! blockRegisters.containsKey(block.getFunction())){
            BlockRegisters b = new BlockRegisters();
            blockRegisters.put(block.getFunction(),b);
        }
        return blockRegisters.get(block.getFunction());
    }

    /** adds the entry point of the given function to the worklist*/
    public void addToWorklist(Function f){
        for(Iterator<QueueEntry> i = worklist.iterator(); i.hasNext();){
            if(i.next().getBlock().equals(f.getEntry())){
                // we don't add it to the worklist because it's already in there
                return;
            }
        }
        worklist.add(new QueueEntry(f.getEntry()));
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
            if(worklist.isEmpty())
                worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
        }

        //todo: add program entry again to the worklist if the store changed or some function state changed since the last flow through
    }
}
