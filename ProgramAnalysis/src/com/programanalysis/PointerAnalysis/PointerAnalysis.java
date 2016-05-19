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
    public PointerAnalysis(Analysis analysis, CallGraphParser cgp, boolean mainOnly, int srcLine, int srcCol){
        this.callGraphParser = cgp;
        this.analysis = analysis;
        this.mainOnly = mainOnly;
        this.srcLine = srcLine;
        this.srcCol = srcCol;
    }

    private int srcLine;

    private int srcCol;

    private int currentMin = Integer.MAX_VALUE;

    private HashSet<AbstractNode> nodesOfInterest;

    private boolean mainOnly;

    private GlobalState state;

    private Transfer visitor;

    private Analysis analysis;

    public static FlowGraph flowgraph;

    public CallGraph callGraph;

    private Map<BasicBlock, BlockRegisters> blockRegisters;

    private PriorityQueue<QueueEntry> worklist;

    private Set<BasicBlock> blockCheckList;

    private CallGraphParser callGraphParser;

    public void init(){
        nodesOfInterest = new HashSet<AbstractNode>();
        flowgraph = analysis.getSolver().getFlowGraph();
        state = new GlobalState();
        callGraph = analysis.getSolver().getAnalysisLatticeElement().getCallGraph();
        blockRegisters = new HashMap<BasicBlock, BlockRegisters>();
        blockCheckList = new HashSet<BasicBlock>();
        worklist = new PriorityQueue<QueueEntry>();
        worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
        if(! mainOnly) {
            // we want to visit all functions and therefore add all here
            for (Function f : flowgraph.getFunctions()) {
                addToWorklist(f);
            }
        }
        visitor = new Transfer(this);
    }

    public boolean getMainOnly(){
        return mainOnly;
    }

    public CallGraphParser getCallGraphParser(){
        return callGraphParser;
    }

    public GlobalState getState(){
        return state;
    }

    public FlowGraph getFlowgraph(){
        return flowgraph;
    }

    public CallGraph getCallgraph() { return callGraph;}

    public HashSet<AbstractNode> getNodesOfInterest(){ return nodesOfInterest;}

    public BlockRegisters getRegisters(BasicBlock block){
        if(! blockRegisters.containsKey(block)){
            BlockRegisters b = new BlockRegisters();
            blockRegisters.put(block,b);
        }
        return blockRegisters.get(block);
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
                if(node.getSourceLocation().getLineNumber() == srcLine  && mainOnly){
                    // the node source line is equal to the wanted node and we add it if it's close enough
                    if(srcCol - node.getSourceLocation().getColumnNumber() >= 0 && srcCol - node.getSourceLocation().getColumnNumber() <= currentMin){
                        if(srcCol - node.getSourceLocation().getColumnNumber() < currentMin){
                            // we have a new minimum, delete the older nodes
                            currentMin = srcCol - node.getSourceLocation().getColumnNumber();
                            nodesOfInterest = new HashSet<AbstractNode>();
                        }
                        nodesOfInterest.add(node);
                    }
                }
                if(node.isRegistersDone()){
                    // delete the registers
                    getRegisters(block).deleteOrdinaryRegisters();
                }
            }
            // add the block successors to the worklist
            for (Iterator<BasicBlock> i = block.getSuccessors().iterator(); i.hasNext(); ) {
                BasicBlock b = i.next();
                // propagate the registers
                getRegisters(b).addRegs(getRegisters(block));
                if(! worklist.contains(new QueueEntry(b)) &&(!blockCheckList.contains(b))){
                    worklist.add(new QueueEntry(b));
                    blockCheckList.add(b);
                }
            }
            if(worklist.isEmpty())
                if(getState().getAndResetChanged()){
                    worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
                    blockCheckList = new HashSet<>();
                    if(! mainOnly) {
                        // we want to visit all functions and therefore add all here
                        for (Function f : flowgraph.getFunctions()) {
                            addToWorklist(f);
                        }
                    }
                }
        }
    }
}
