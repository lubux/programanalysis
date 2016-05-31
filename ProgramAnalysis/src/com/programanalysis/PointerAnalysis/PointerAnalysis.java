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

    private int maxIter = 12;

    private Integer abstractObjectIndex = 0;

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

    private AbstractObject theGlobalObject;

    public Integer getAndIncAbsObjIdx(){
        Integer res = new Integer(abstractObjectIndex.intValue());
        abstractObjectIndex++;
        return res;
    }

    public AbstractObject getTheGlobalObject(){
        return theGlobalObject;
    }

    public void init(){
        nodesOfInterest = new HashSet<AbstractNode>();
        flowgraph = analysis.getSolver().getFlowGraph();
        state = new GlobalState(this);
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
        // create an abstract object for document, window, console

        // document
        AbstractObject absObj = new AbstractObject(abstractObjectIndex.toString());
        Set<AbstractObject> set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("document", flowgraph.getMain(), set);

        // define the global object
        theGlobalObject = absObj;

        // add the global object to the this object of the main function
        getState().getInstate(flowgraph.getMain()).thisObjects.add(absObj);

        // console
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("console", flowgraph.getMain(), set);

        //window
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("window", flowgraph.getMain(), set);

        //screen
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("screen", flowgraph.getMain(), set);

        //location
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("location", flowgraph.getMain(), set);

        //process
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("process", flowgraph.getMain(), set);

        //Number
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("Number", flowgraph.getMain(), set);

        //Math
        absObj = new AbstractObject(abstractObjectIndex.toString());
        set = new HashSet<AbstractObject>();
        set.add(absObj);
        abstractObjectIndex++;
        getState().writeStore("Math", flowgraph.getMain(), set);

        // start with the analysis
        boolean first = true;
        int iterCounter = 0;
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
                    iterCounter++;
                    if(iterCounter < maxIter) {
                        worklist.add(new QueueEntry(flowgraph.getEntryBlock()));
                        blockCheckList = new HashSet<>();
                        if (!mainOnly) {
                            // we want to visit all functions and therefore add all here
                            for (Function f : flowgraph.getFunctions()) {
                                addToWorklist(f);
                            }
                        }
                    }
                }
            if(worklist.isEmpty() && first && !mainOnly){
                // we have completed the pointer analysis, now we put abstract objects into each empty function argument
                for(Function f: flowgraph.getFunctions()){
                    InState inState = state.getInstate(f);
                    for(String name: f.getParameterNames()){
                        if(!inState.argumentObjects.keySet().contains(name)){
                            Set<AbstractObject> inSet = new HashSet<AbstractObject>();
                            inState.argumentObjects.put(name, inSet);
                        }
                        if(inState.argumentObjects.get(name).isEmpty()) {
                            // there is no abstract object behind this argument and we put one there
                            AbstractObject obj = new AbstractObject(abstractObjectIndex.toString());
                            abstractObjectIndex++;
                            inState.argumentObjects.get(name).add(obj);
                        }
                        if(inState.thisObjects.isEmpty()){
                            // there is no abstract object behind the this object, so we put one there
                            AbstractObject obj = new AbstractObject(abstractObjectIndex.toString());
                            abstractObjectIndex++;
                            inState.thisObjects.add(obj);
                        }
                    }
                }
                first = false;
                // re-run the analysis
                blockCheckList = new HashSet<>();
                // we want to visit all functions and therefore add all here
                for (Function f : flowgraph.getFunctions()) {
                    addToWorklist(f);
                }

            }
        }
    }
}
