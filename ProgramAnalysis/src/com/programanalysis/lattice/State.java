package com.programanalysis.lattice;

import com.programanalysis.monitoring.iMonitoring;
import com.programanalysis.util.Tuple;
import dk.brics.tajs.flowgraph.BasicBlock;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.solver.GenericSolver;
import dk.brics.tajs.solver.IState;
import com.programanalysis.util.VariableName;

import java.util.*;

/**
 * Created by cedri on 4/24/2016.
 */
public class State implements IState <State, Context, CallEdge> {


    /** store from variable name to abstract object*/
    private static Map<String, Set<AbstractObject>> store = new HashMap<String, Set<AbstractObject>>();;

    /** store from abstract object and field to abstract object*/
    private static Map<Tuple<AbstractObject, String>, Set<AbstractObject>> propertystore = new HashMap<Tuple<AbstractObject, String>, Set<AbstractObject>>();

    private BasicBlock block;

    private Context context;

    private List<Object> registers;

    private GenericSolver<State, Context, CallEdge, iMonitoring, ?>.SolverInterface solver;


    public State(GenericSolver<State, Context, CallEdge, iMonitoring, ?>.SolverInterface c, BasicBlock block){
        this.block = block;
        this.solver = c;
    }

    public GenericSolver<State, Context, CallEdge, iMonitoring, ?>.SolverInterface getSolverInterface(){return solver;}

    private State(State s){
        registers = new ArrayList<Object>(s.registers);
        block = s.block;
        context = s.context;
    }

    /** creates a new set for the given variable in the store (used for variabledeclaraton nodes)*/
    public void variableDeclaration(String variable, Function scope){
        Set<AbstractObject> s = new HashSet<AbstractObject>();
        // set will already exists when we traverse the flow graph the second time
        if(! store.containsKey(VariableName.getVariableName(scope, variable, this)))
            store.put(VariableName.getVariableName(scope, variable, this), s);
    }

    /** adds obj to the store set of variable in the given scope*/
    public void writeStore(String variable, Function scope, AbstractObject obj){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable, this))){
            scope = scope.getOuterFunction();
        }
        //TODO: global object property if var is not found
        // put the abstract object in the variable store
        store.get(VariableName.getVariableName(scope, variable, this)).add(obj);
    }

    /** adds obj to the store set of variable in the given scope*/
    public void writeStore(String variable, Function scope, Set<AbstractObject> objs){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable, this))){
            scope = scope.getOuterFunction();
        }
        //TODO: global object property if var is not found
        // put the abstract object in the variable store
        store.get(VariableName.getVariableName(scope, variable, this)).addAll(objs);
    }

    public Set<AbstractObject> readStore(String variable, Function scope){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable, this))){
            scope = scope.getOuterFunction();
        }
        return store.get(VariableName.getVariableName(scope, variable, this));
    }

    /** adds prop to the propertystore of obj and property */
    public void writePropertyStore(AbstractObject obj, String property, AbstractObject prop){
        if(! propertystore.containsKey(new Tuple<AbstractObject, String>(obj, property))){
            Set<AbstractObject> s = new HashSet<>();
            s.add(prop);
            propertystore.put(new Tuple<AbstractObject, String>(obj, property), s);
        } else {
            propertystore.get(new Tuple<AbstractObject, String>(obj, property)).add(prop);
        }
    }
    /** adds prop to all properties of onj*/
    public void writeAllPropertyStore(AbstractObject obj, AbstractObject prop){
        for(Tuple<AbstractObject,String>t:propertystore.keySet()){
            if(t.a.equals(obj)){
                propertystore.get(t).add(prop);
            }
        }
    }

    /** adds prop to all properties of onj*/
    public void writeAllPropertyStore(AbstractObject obj, Set<AbstractObject> prop){
        for(Tuple<AbstractObject,String>t:propertystore.keySet()){
            if(t.a.equals(obj)){
                propertystore.get(t).addAll(prop);
            }
        }
    }

    /** adds prop to the propertystore of obj and property */
    public void writePropertyStore(AbstractObject obj, String property, Set<AbstractObject> prop){
        if(! propertystore.containsKey(new Tuple<AbstractObject, String>(obj, property))){
            Set<AbstractObject> s = new HashSet<>();
            s.addAll(prop);
            propertystore.put(new Tuple<AbstractObject, String>(obj, property), s);
        } else {
            propertystore.get(new Tuple<AbstractObject, String>(obj, property)).addAll(prop);
        }
    }

    public void writeRegister(int reg,Object obj){
        registers.set(reg, obj);
    }

    public Object readRegister(int reg){
        return registers.get(reg);
    }

    @Override
    public State clone() {
        return new State(this);
    }

    @Override
    public boolean propagate(State state, boolean b) {
        boolean result = false;
        // TODO: adapt generic solver because this will always return false because of the global stores
        /*
        Set<AbstractObject> set;
        for(Map.Entry<String, Set<AbstractObject>> i: state.store.entrySet()){
            set = i.getValue();
            if(store.containsKey(i.getKey())){
                result |= store.get(i.getKey()).addAll(set);
            } else {
                result = true;
                store.put(i.getKey(), new HashSet<AbstractObject>(i.getValue()));
            }
        }
        for(Map.Entry<Tuple<AbstractObject, String>, Set<AbstractObject>> i: state.propertystore.entrySet()){
            set = i.getValue();
            if(propertystore.containsKey(i.getKey())){
                result |= propertystore.get(i.getKey()).addAll(set);
            } else {
                result = true;
                propertystore.put(i.getKey(), new HashSet<AbstractObject>(i.getValue()));
            }
        }*/
        return result;
    }

    @Override
    public boolean isNone() {return store.isEmpty() || propertystore.isEmpty();
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
