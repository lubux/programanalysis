package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.QueueEntry;
import com.programanalysis.util.Tuple;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.FlowGraph;
import dk.brics.tajs.flowgraph.Function;

import java.util.*;
import java.util.Collections;

/**
 * Created by cedri on 5/8/2016.
 */
public class HistoryCreation {

    private Analysis analysis;

    private boolean mainOnly;

    private FlowGraph flowGraph;

    private PointerAnalysis pointerAnalysis;

    private PriorityQueue<QueueEntry> workList;

    private Map<BasicBlock, Integer> blockCounter;

    private State state;

    private State oldState;

    private Transfer visitor;

    private int maxIter = 12;

    private Set<AbstractNode> nodesOfInterest;

    private Set<AbstractObject> predictionObjects;

    private String variableName;

    private boolean prediction;

    public HistoryCreation(Analysis analysis, PointerAnalysis pointerAnalysis){
        // initialize all data structures
        this.analysis = analysis;
        this.pointerAnalysis = pointerAnalysis;
        this.flowGraph = analysis.getSolver().getFlowGraph();
        this.workList = new PriorityQueue<QueueEntry>();
        this.blockCounter = new HashMap<BasicBlock, Integer>();
        this.state = new State();
        this.oldState = new State();
        this.visitor = new Transfer(pointerAnalysis, this);
        this.prediction = false;
        this.nodesOfInterest = new HashSet<AbstractNode>();
        this.predictionObjects = new HashSet<AbstractObject>();
        this.mainOnly = false;
    }

    /** constructor for history extraction/ prediction -> prediction if variable name = null*/
    public HistoryCreation(Analysis analysis, PointerAnalysis pointerAnalysis, String variableName, Set<AbstractNode> nodesOfInterest){
        // initialize all data structures
        this.analysis = analysis;
        this.pointerAnalysis = pointerAnalysis;
        this.flowGraph = analysis.getSolver().getFlowGraph();
        this.workList = new PriorityQueue<QueueEntry>();
        this.blockCounter = new HashMap<BasicBlock, Integer>();
        this.state = new State();
        this.oldState = new State();
        this.visitor = new Transfer(pointerAnalysis, this);
        this.predictionObjects = new HashSet<AbstractObject>();
        this.mainOnly = true;
        this.nodesOfInterest = nodesOfInterest;
        if(variableName == null){
            prediction = true;
        } else {
            prediction = false;
        }
        this.variableName = variableName;
    }

    public State getState(){
        return state;
    }

    public State getOldState(){
        return oldState;
    }

    public boolean getPrediction(){
        return prediction;
    }

    public Set<AbstractNode> getNodesOfInterest(){
        return nodesOfInterest;
    }

    public Set<AbstractObject> getPredictionObjects(){
        return predictionObjects;
    }

    public boolean getMainOnly(){
        return mainOnly;
    }

    public void addToWorklist(QueueEntry entry){
        // add a history for all the objects in the function and its outer functions
        Function f = entry.getBlock().getFunction();
        while(f != null){
            Map<String, Set<AbstractObject>> store = pointerAnalysis.getState().getStore(f);
            if(store != null){
                for(String s:store.keySet()){
                    Set<AbstractObject> set = store.get(s);
                    for(AbstractObject obj: set){
                        if(!state.getBlockInState(entry.getBlock()).keySet().contains(obj)){
                            History h = new History(obj);
                            state.getBlockInState(entry.getBlock()).put(obj, h);
                        }
                    }
                }
            }
            f = f.getOuterFunction();
        }
        // add a history for all the objects in the property store
        Map<Tuple, Set<AbstractObject>> propertyStore = pointerAnalysis.getState().getPropertystore();
        for(Tuple t: propertyStore.keySet()){
            for(AbstractObject obj: propertyStore.get(t)){
                if(!state.getBlockInState(entry.getBlock()).keySet().contains(obj)){
                    History h = new History(obj);
                    state.getBlockInState(entry.getBlock()).put(obj, h);
                }
            }
        }

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
                    blockCounter.put(entry.getBlock(), ++i);
                    workList.add(entry);
                } else {
                    // we don't add it because it's already in the worklist, but we also don't increase the counter
                }
            }
        }
    }

    public void solve(){
        int iterationCount = 0;
        // add all function entries to the worklist
        addToWorklist(new QueueEntry(flowGraph.getEntryBlock()));
        if(!mainOnly) {
            // we are creating training data and want to add all functions
            // this won't add the main function 2 times because of the check in the addToWorklist method*/
            for (Function f : flowGraph.getFunctions()) {
                addToWorklist(new QueueEntry(f.getEntry()));
            }
        }

        // iterate to the fixpoint
        while(! workList.isEmpty()){
            QueueEntry entry = workList.remove();
            BasicBlock block = entry.getBlock();
            state.setCurrentState(block);
            // go through the nodes of the basic block
            for(AbstractNode node: block.getNodes()){
                visitor.transfer(node, null);
                if(node.isRegistersDone()){
                    // delete the registers
                    getState().getRegisters(block).deleteOrdinaryRegisters();
                }
            }

            // add the block successors to the worklist
            for (Iterator<BasicBlock> i = block.getSuccessors().iterator(); i.hasNext(); ) {
                BasicBlock b = i.next();
                // propagate the registers
                getState().getRegisters(b).addRegs(getState().getRegisters(block));
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
            if(block.getSuccessors().isEmpty() || block.getFunction().getOrdinaryExit().equals(block)
                    || block.getFunction().getExceptionalExit().equals(block)){
                // add the current state to the out state
                Map<AbstractObject, History> outState = state.getFunctionOutState(block.getFunction());
                for(AbstractObject absObj: state.getCurrentState().keySet()){
                    if(! outState.keySet().contains(absObj)){
                        outState.put(absObj, state.getCurrentState().get(absObj).copy());
                    } else {
                        // merge the current state in the out state
                        outState.get(absObj).merge(state.getCurrentState().get(absObj));
                    }
                }
            }
            if(workList.isEmpty())
                if(! state.equals(oldState) && iterationCount < maxIter){
                    // we start the whole iteration again
                    iterationCount = iterationCount + 1;
                    oldState = state;
                    state = new State();
                    blockCounter = new HashMap<BasicBlock, Integer>();
                    // add all function entries to the worklist
                    addToWorklist(new QueueEntry(flowGraph.getEntryBlock()));
                    if(!mainOnly) {
                        // we are creating training data add all functions
                        for (Function f : flowGraph.getFunctions()) {
                            addToWorklist(new QueueEntry(f.getEntry()));
                        }
                    }

                } else {
                    // we have a fix point
                }
        }
    }

    /** prints all histories contained in this analysis to a string, each history on a new line, be aware that there is randomness in the history selection*/
    public String printAllHistories(){
        String res = "";
        for(Function f: flowGraph.getFunctions()){
            Map<AbstractObject, History> map = state.getFunctionOutState(f);
            for(AbstractObject obj: map.keySet()){
                History h = map.get(obj);
                res = res + h.print();
            }
        }
        return res;
    }

    public String printSelectedHistories(){
        String res = "";
        boolean first = true;
        Map<Function, Set<Function>> callGraph = pointerAnalysis.getCallGraphParser().getHistoryCallGraph();
        Queue<Function> removeQueue = new LinkedList<Function>();
        Set<Function> printSet = new HashSet<Function>();
        Map<Function, Integer> calledFunctionsCounter = new HashMap<Function, Integer>();
        for(Function f: flowGraph.getFunctions()){
            calledFunctionsCounter.put(f, 0);
        }
        removeQueue.add(flowGraph.getMain());
        printSet.add(flowGraph.getMain());
        while(! removeQueue.isEmpty()){
            Function f = removeQueue.remove();
            calledFunctionsCounter.remove(f);
            if(callGraph.keySet().contains(f)){
                Set<Function> set = callGraph.get(f);
                for(Function g: set){
                    if(!removeQueue.contains(g)) {
                        removeQueue.add(g);
                    }
                }
                callGraph.remove(f);
            }
            if(removeQueue.isEmpty() && calledFunctionsCounter.keySet().size() > 0){
                if(first){
                    // traverse the rest of the call graph and count how many times each remaining function is called
                    for(Function key: callGraph.keySet()){
                        for(Function value: callGraph.get(key)){
                            if(calledFunctionsCounter.keySet().contains(value)){
                                // the called function is still of our interest
                                calledFunctionsCounter.put(value, calledFunctionsCounter.get(value) + 1);
                            }
                        }
                    }
                    first = false;
                }
                // as we have counted how many times each function is called, we pick the one that is called the least times (ideally 0)
                int min = Collections.min(calledFunctionsCounter.values());
                //pick the first function that is called 'min' times
                for(Function g: calledFunctionsCounter.keySet()){
                    if(calledFunctionsCounter.get(g).intValue() == min){
                        printSet.add(g);
                        removeQueue.add(g);
                        break;
                    }
                }
            }
        }
        // merge all histories in a new Map in order to avoid duplicate histories from same abstract objects(due to context insensitive pointer analysis)
        Map<AbstractObject, History> finalHistoryMap = new HashMap<AbstractObject, History>();
        for(Function f: printSet){
            Map<AbstractObject, History> historyMap = state.getFunctionOutState(f);
            for(AbstractObject obj: historyMap.keySet()){
                if(!finalHistoryMap.keySet().contains(obj)){
                    finalHistoryMap.put(obj, historyMap.get(obj));
                } else {
                    // merge the other history into the final history
                    finalHistoryMap.get(obj).merge(historyMap.get(obj));
                }
            }
        }

        for(AbstractObject obj: finalHistoryMap.keySet()){
            History h = finalHistoryMap.get(obj);
            res = res + h.print();
        }

        return res;
    }

    /** returns the all the histories of the main flow of the variable specified in function with name identifier*/
    public String printVariableHistories(Function function, String identifier){
        String res = "";
        Set<AbstractObject> objectSet = pointerAnalysis.getState().readStore(identifier, function);
        Map<AbstractObject, History> map = state.getFunctionOutState(flowGraph.getMain());
        for(AbstractObject obj: objectSet){
            History h = map.get(obj);
            if(h != null){
                res = res + h.print();
            }
        }
        return res;
    }

    /** print all histories that are used for the prediction*/
    public String printPredictionHistories(){
        String res = "";
        for(AbstractObject obj: predictionObjects){
            History h = state.getFunctionOutState(flowGraph.getMain()).get(obj);
            res = res + h.printPredictionHistory();
        }
        return res;
    }

    public String printExtractionHistories(int progId, int nodeId){
        String res = "";
        Function f = null;
        Set<AbstractObject> absObjs;
        // get the function of the desired variable (might do something less ugly here
        for(AbstractNode node: nodesOfInterest){
            f = node.getBlock().getFunction();
            break;
        }
        if(f == null){
            return res;
        }
        if(f.getParameterNames().contains(variableName)){
            // we want to extract the histories of a function parameter
            absObjs = pointerAnalysis.getState().getInstate(f).argumentObjects.get(variableName);
        } else {
            // we have an ordinary variable
            absObjs = pointerAnalysis.getState().readStore(variableName, f);
        }
        for(AbstractObject obj: absObjs){
            History h = state.getFunctionOutState(flowGraph.getMain()).get(obj);
            if(h != null){
                res = res + h.printExtractionHistory(progId, nodeId);
            }
        }

        return res;
    }
}
