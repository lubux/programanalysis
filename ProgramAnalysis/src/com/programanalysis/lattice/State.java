package com.programanalysis.lattice;

import com.programanalysis.monitoring.iMonitoring;
import com.programanalysis.util.Tuple;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.monitoring.IAnalysisMonitoring;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.solver.ICallEdge;
import dk.brics.tajs.solver.IContext;
import dk.brics.tajs.solver.IState;
import com.programanalysis.lattice.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cedri on 4/24/2016.
 */
public class State implements IState <State, Context, CallEdge> {


    /** store from global variable name to abstract object*/
    private Map<String, Set<AbstractObject>> globalstore;

    /** store from local variable name to abstract object*/
    private Map<String, Set<AbstractObject>> localstore;

    /** store from abstract object and field to abstract object*/
    private Map<Tuple<AbstractObject, String>, Set<AbstractObject>> fieldstore;

    private BasicBlock block;

    private Context context;

    /** used for the node visitor of newobjectnode*/
    public AbstractObject newObject;


    public State(GenericSolver<State, Context, CallEdge, iMonitoring, ?>.SolverInterface c, BasicBlock block){
        this.block = block;
    }

    private State(State s){
        block = s.block;
        context = s.context;
        globalstore = new HashMap<String, Set<AbstractObject>>();
        localstore = new HashMap<String, Set<AbstractObject>>();
        fieldstore = new HashMap<Tuple<AbstractObject, String>, Set<AbstractObject>>();
        Set<AbstractObject> set;
        for(Map.Entry<String, Set<AbstractObject>> i: s.globalstore.entrySet()){
            set = new HashSet<AbstractObject>(i.getValue());
            globalstore.put(i.getKey(), set);
        }
        for(Map.Entry<String, Set<AbstractObject>> i: s.localstore.entrySet()){
            set = new HashSet<AbstractObject>(i.getValue());
            localstore.put(i.getKey(), set);
        }
        for(Map.Entry<Tuple<AbstractObject, String>, Set<AbstractObject>> i: s.fieldstore.entrySet()){
            set = new HashSet<AbstractObject>(i.getValue());
            fieldstore.put(i.getKey(), set);
        }
    }


    @Override
    public State clone() {
        return new State(this);
    }

    @Override
    public boolean propagate(State state, boolean b) {
        boolean result = false;
        Set<AbstractObject> set;
        for(Map.Entry<String, Set<AbstractObject>> i: state.globalstore.entrySet()){
            set = i.getValue();
            if(globalstore.containsKey(i.getKey())){
                result |= globalstore.get(i.getKey()).addAll(set);
            } else {
                result = true;
                globalstore.put(i.getKey(), new HashSet<AbstractObject>(i.getValue()));
            }
        }
        for(Map.Entry<String, Set<AbstractObject>> i: state.localstore.entrySet()){
            set = i.getValue();
            if(localstore.containsKey(i.getKey())){
                result |= localstore.get(i.getKey()).addAll(set);
            } else {
                result = true;
                localstore.put(i.getKey(), new HashSet<AbstractObject>(i.getValue()));
            }
        }
        for(Map.Entry<Tuple<AbstractObject, String>, Set<AbstractObject>> i: state.fieldstore.entrySet()){
            set = i.getValue();
            if(fieldstore.containsKey(i.getKey())){
                result |= fieldstore.get(i.getKey()).addAll(set);
            } else {
                result = true;
                fieldstore.put(i.getKey(), new HashSet<AbstractObject>(i.getValue()));
            }
        }
        return result;
    }

    @Override
    public boolean isNone() {
        return globalstore.isEmpty() || localstore.isEmpty() || fieldstore.isEmpty();
    }

    @Override
    public String toStringBrief() {
        return null;
    }

    @Override
    public String toDot() {
        return null;
    }

    @Override
    public String diff(State state) {
        return null;
    }

    @Override
    public void localize(State state) {

    }

    @Override
    public Context transform(CallEdge callEdge, Context context, Map<Context, State> map, BasicBlock basicBlock) {
        return context;
    }

    @Override
    public boolean transformInverse(CallEdge callEdge, BasicBlock basicBlock, Context context) {
        return false;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
