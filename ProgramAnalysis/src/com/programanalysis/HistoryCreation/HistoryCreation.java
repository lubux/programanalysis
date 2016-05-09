package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.CallGraphParser;
import com.programanalysis.util.QueueEntry;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.flowgraph.Function;

import java.util.*;

/**
 * Created by cedri on 5/8/2016.
 */
public class HistoryCreation {

    private Analysis analysis;

    private FlowGraph flowGraph;

    private PointerAnalysis pointerAnalysis;

    private PriorityQueue<QueueEntry> workList;

    private Map<BasicBlock, Integer> blockCounter;

    private State state;

    private State oldState;

    private Transfer visitor;

    public HistoryCreation(Analysis analysis, PointerAnalysis pointerAnalysis){
        // initialize all data structures
        this.analysis = analysis;
        this.pointerAnalysis = pointerAnalysis;
        this.flowGraph = analysis.getSolver().getFlowGraph();
        this.workList = new PriorityQueue<QueueEntry>();
        this.blockCounter = new HashMap<BasicBlock, Integer>();
        this.state = new State();
        this.oldState = this.state;
        this.visitor = new Transfer(pointerAnalysis, this);
    }

    public void addToWorklist(QueueEntry entry){
        if(! blockCounter.keySet().contains(entry.getBlock())){
            blockCounter.put(entry.getBlock(), 1);
            workList.add(entry);
        } else {
            Integer i = blockCounter.get(entry.getBlock());
            if(i >=2){
                // we don't add it to the worklist because it was already added 2 times
                return;
            } else {
                if(!workList.contains(entry)){
                    blockCounter.put(entry.getBlock(), i++);
                    workList.add(entry);
                } else {
                    // we don't add it because it's already in the worklist, but we also don't increase the counter
                }
            }
        }
    }

    public void solve(){
        // add all function entries to the worklist
        addToWorklist(new QueueEntry(flowGraph.getEntryBlock()));
        // this won't add the main function 2 times because of the check in the addToWorklist method*/
        for(Function f: flowGraph.getFunctions()){
            addToWorklist(new QueueEntry(f.getEntry()));
        }

        // iterate to the fixpoint
        while(! workList.isEmpty()){
            QueueEntry entry = workList.remove();
            BasicBlock block = entry.getBlock();
            state.setCurrentState(block);
            // go through the nodes of the basic block
            for(AbstractNode node: block.getNodes()){
                visitor.transfer(node, null);
            }

            //TODO: merge current state to successors in state
            // add the block successors to the worklist
            for (Iterator<BasicBlock> i = block.getSuccessors().iterator(); i.hasNext(); ) {
                BasicBlock b = i.next();
                // propagate the histories to the successor block
                Map<AbstractObject, History> inState = state.getBlockInState(b);
                for(AbstractObject obj: state.getCurrentState().keySet()){
                    if(inState.keySet().contains(obj)){
                        // we merge the histories
                        inState.get(obj).merge(state.getCurrentState().get(obj));
                    } else {
                        // we add a copy of the history to the in state
                        inState.put(obj, state.getCurrentState().get(obj).copy());
                    }
                }
                // add the successor to the worklist
                addToWorklist(new QueueEntry(b));
            }
            if(workList.isEmpty())
                if(! state.equals(oldState)){
                    // we start the whole iteration again
                    oldState = state;
                    state = new State();
                    blockCounter = new HashMap<BasicBlock, Integer>();
                    // add all function entries to the worklist
                    addToWorklist(new QueueEntry(flowGraph.getEntryBlock()));
                    for(Function f: flowGraph.getFunctions()){
                        addToWorklist(new QueueEntry(f.getEntry()));
                    }

                } else {
                    // we have a fix point
                }
        }
    }
}
