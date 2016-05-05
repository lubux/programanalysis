package com.programanalysis.PointerAnalysis;

import com.programanalysis.util.CallGraphParser;
import com.programanalysis.util.QueueEntry;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.solver.CallGraph;

import java.util.*;

/**
 * Created by cedri on 4/28/2016.
 */
public class PointerAnalysis {
    public PointerAnalysis(Analysis analysis, CallGraphParser cgp){
        this.callGraphParser = cgp;
        this.analysis = analysis;
    }

    private GlobalState state;

    private Transfer visitor;

    private Analysis analysis;

    public static FlowGraph flowgraph;

    // TODO: might remove this?
    public static CallGraph callgraph;

    private Map<Function, BlockRegisters> blockRegisters;

    private PriorityQueue<QueueEntry> worklist;

    private Set<BasicBlock> blockCheckList;

    private CallGraphParser callGraphParser;

    public void init(){
        state = new GlobalState();
        flowgraph = analysis.getSolver().getFlowGraph();
        callgraph = analysis.getSolver().getAnalysisLatticeElement().getCallGraph();
        blockRegisters = new HashMap<Function, BlockRegisters>();
        worklist = new PriorityQueue<QueueEntry>();
        worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
        visitor = new Transfer(this);
        blockCheckList = new HashSet<BasicBlock>();
    }

    public CallGraphParser getCallGraphParser(){
        return callGraphParser;
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
            if(i.next().getBlock().equals(f.getEntry()) || blockCheckList.contains(f.getEntry())){
                // we don't add it to the worklist because it's already in there
                return;
            }
        }
        worklist.add(new QueueEntry(f.getEntry()));
        blockCheckList.add(f.getEntry());
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
                BasicBlock b = i.next();
                if(! worklist.contains(new QueueEntry(b)) &&(!blockCheckList.contains(b))){
                    worklist.add(new QueueEntry(b));
                    blockCheckList.add(b);
                }
            }
            if(worklist.isEmpty())
                if(getState().getAndResetChanged()){
                    worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
                    blockCheckList = new HashSet<>();
                }
        }
    }
}
