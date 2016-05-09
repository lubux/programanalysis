package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cedri on 5/9/2016.
 */
public class State {
    private Map<BasicBlock, Map<AbstractObject, History>> blockInStates;

    private Map<Function, Map<AbstractObject, History>> outStates;

    private Map<AbstractObject, History> currentState;

    public State(){
        this.blockInStates = new HashMap<BasicBlock, Map<AbstractObject, History>>();
        this.outStates = new HashMap<Function, Map<AbstractObject, History>>();
    }

    public void setCurrentState(BasicBlock block){
        if(!blockInStates.keySet().contains(block)){
            // use empty current state
            currentState = new HashMap<AbstractObject, History>();
        } else {
            // copy the in state to the current state
            currentState = new HashMap<AbstractObject, History>();
            for(AbstractObject obj: blockInStates.get(block).keySet()){
                currentState.put(obj,blockInStates.get(block).get(obj).copy());
            }
        }
    }

    public Map<AbstractObject, History> getBlockInState(BasicBlock block){
        if(!blockInStates.keySet().contains(block)){
            Map<AbstractObject, History> map = new HashMap<AbstractObject, History>();
            blockInStates.put(block, map);
        }
        return blockInStates.get(block);
    }

    public Map<AbstractObject, History> getCurrentState(){
        return currentState;
    }
}
