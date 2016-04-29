package com.programanalysis.PointerAnalysis;

import com.programanalysis.lattice.AbstractObject;
import com.programanalysis.util.Tuple;
import com.programanalysis.util.VariableName;
import dk.brics.tajs.flowgraph.Function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cedri on 4/28/2016.
 * contains the global store that is used for the entire program
 */
public class GlobalState {

    /** has the GlobalState changed since the last changed query?*/
    private boolean changed;

    private Map<Function, InState> inStates;

    private Map<Function, OutState> outStates;

    public GlobalState(){
        store = new HashMap<String, Set<AbstractObject>>();
        propertystore = new HashMap<Tuple<AbstractObject, String>, Set<AbstractObject>>();
        inStates = new HashMap<Function, InState>();
        outStates = new HashMap<Function, OutState>();
    }

    /** this should only be used to read the state*/
    public InState getInstate(Function f){
        if(! inStates.containsKey(f)){
            InState state = new InState();
            inStates.put(f, state);
        }
        return inStates.get(f);
    }

    /** this should only be used to read the state*/
    public OutState getOutstate(Function f){
        if(! outStates.containsKey(f)){
            OutState state = new OutState();
            outStates.put(f, state);
        }
        return outStates.get(f);
    }

    /** add all the AbstractObjects in objs to the OutState of f*/
    public void addToOutState(Set<AbstractObject> objs, Function f){
        if(! outStates.containsKey(f)){
            OutState state = new OutState();
            outStates.put(f, state);
        }
        changed |= outStates.get(f).returnObjects.addAll(objs);
    }

    /** add all the AbstractObjects in objs to the this objects in InState of f*/
    public void addThisToInState(Set<AbstractObject> objs, Function f){
        if(! inStates.containsKey(f)){
            InState state = new InState();
            inStates.put(f, state);
        }
        changed |= inStates.get(f).thisObjects.addAll(objs);
    }

    /** add all the AbstractObjects in objs to the set of argument arg in InState of f*/
    public void addArgToInState(Set<AbstractObject> objs, Function f, String arg){
        if(! inStates.containsKey(f)){
            InState state = new InState();
            inStates.put(f, state);
        }
        Map<String,Set<AbstractObject>> args = inStates.get(f).argumentObjects;
        if(! args.containsKey(arg)){
            Set<AbstractObject> set = new HashSet<AbstractObject>();
            args.put(arg, set);
        }
        changed |= args.get(arg).addAll(objs);
    }



    /** store from variable name to abstract object*/
    private Map<String, Set<AbstractObject>> store;

    /** store from abstract object and field to abstract object*/
    private Map<Tuple<AbstractObject, String>, Set<AbstractObject>> propertystore;

    /** creates a new set for the given variable in the store (used for variabledeclaraton nodes)*/
    public void variableDeclaration(String variable, Function scope){
        Set<AbstractObject> s = new HashSet<AbstractObject>();
        // set will already exists when we traverse the flow graph the second time
        if(! store.containsKey(VariableName.getVariableName(scope, variable)))
            store.put(VariableName.getVariableName(scope, variable), s);
    }

    /** adds obj to the store set of variable in the given scope*/
    public void writeStore(String variable, Function scope, AbstractObject obj){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable))){
            scope = scope.getOuterFunction();
        }
        //TODO: write global object property if var is not found
        // put the abstract object in the variable store
        store.get(VariableName.getVariableName(scope, variable)).add(obj);
    }

    /** adds obj to the store set of variable in the given scope*/
    public void writeStore(String variable, Function scope, Set<AbstractObject> objs){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable))){
            scope = scope.getOuterFunction();
        }
        //TODO: global object property if var is not found
        // put the abstract object in the variable store
        store.get(VariableName.getVariableName(scope, variable)).addAll(objs);
    }

    public Set<AbstractObject> readStore(String variable, Function scope){
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable))){
            scope = scope.getOuterFunction();
            if(scope == null){
                return null;
            }
        }
        return store.get(VariableName.getVariableName(scope, variable));
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
}
