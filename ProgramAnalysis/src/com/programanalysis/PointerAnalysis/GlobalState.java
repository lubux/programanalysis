package com.programanalysis.PointerAnalysis;

import com.programanalysis.util.Tuple;
import com.programanalysis.util.VariableName;
import com.sun.javafx.binding.SelectBinding;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.Function;

import java.awt.*;
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

    private Map<Function, Map<String, Set<AbstractObject>>> store;

    /** store from abstract object and field to abstract object*/
    private Map<Tuple, Set<AbstractObject>> propertystore;

    public GlobalState(){
        store = new HashMap<Function, Map<String, Set<AbstractObject>>>();
        for(Function f: PointerAnalysis.flowgraph.getFunctions()){
            Map<String, Set<AbstractObject>> map = new HashMap<String, Set<AbstractObject>>();
            store.put(f, map);
        }
        propertystore = new HashMap<Tuple, Set<AbstractObject>>();
        inStates = new HashMap<Function, InState>();
        outStates = new HashMap<Function, OutState>();
    }

    /** returns the value of the changed boolean and resets it to false afterwards*/
    public boolean getAndResetChanged(){
        boolean res = changed;
        changed = false;
        return res;
    }

    /** this should only be used to read the state*/
    public InState getInstate(Function f){
        if(! inStates.containsKey(f)){
            InState state = new InState();
            inStates.put(f, state);
            changed = true;
        }
        return inStates.get(f);
    }

    /** this should only be used to read the state*/
    public OutState getOutstate(Function f){
        if(! outStates.containsKey(f)){
            OutState state = new OutState();
            outStates.put(f, state);
            changed = true;
        }
        return outStates.get(f);
    }

    /** add all the AbstractObjects in objs to the OutState of f*/
    public void addToOutState(Set<AbstractObject> objs, Function f){
        if(objs == null){
            return;
        }
        if(! outStates.containsKey(f)){
            OutState state = new OutState();
            outStates.put(f, state);
        }
        changed |= outStates.get(f).returnObjects.addAll(objs);
    }

    /** add all the AbstractObjects in objs to the this objects in InState of f*/
    public void addThisToInState(Set<AbstractObject> objs, Function f){
        if(objs == null){
            return;
        }
        if(! inStates.containsKey(f)){
            InState state = new InState();
            inStates.put(f, state);
        }
        changed |= inStates.get(f).thisObjects.addAll(objs);
    }

    /** add all the AbstractObjects in objs to the set of argument arg in InState of f*/
    public void addArgToInState(Set<AbstractObject> objs, Function f, String arg){
        if(objs == null){
            return;
        }
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


    /** creates a new set for the given variable in the store (used for variabledeclaraton nodes)*/
    public void variableDeclaration(String variable, Function scope){
        if(scope == null){
            return;
        }
        if(! store.keySet().contains(scope)){
            Map<String, Set<AbstractObject>> map = new HashMap<String, Set<AbstractObject>>();
            store.put(scope, map);
        }
        Map<String, Set<AbstractObject>> functionStore = store.get(scope);
        // set will already exists when we traverse the flow graph the second time
        if(! functionStore.containsKey(variable)) {
            Set<AbstractObject> s = new HashSet<AbstractObject>();
            functionStore.put(variable, s);
            changed = true;
        }
    }

    /** adds obj to the store set of variable in the given scope*/
    /*public void writeStore(String variable, Function scope, AbstractObject obj){
        if(obj == null){
            return;
        }
        // switch to the outer scope until the variable is defined
        while(! store.containsKey(VariableName.getVariableName(scope, variable))){
            scope = scope.getOuterFunction();
        }
        //TODO: write global object property if var is not found
        // put the abstract object in the variable store
        changed |= store.get(VariableName.getVariableName(scope, variable)).add(obj);
    }*/

    /** adds obj to the store set of variable in the given scope*/
    public void writeStore(String variable, Function scope, Set<AbstractObject> objs){
        if(objs == null){
            return;
        }
        // switch to the outer scope until the variable is defined
        while(! store.get(scope).containsKey(variable)){
            if(scope.getOuterFunction() != null) {
                scope = scope.getOuterFunction();
            } else {
                if(! store.get(scope).containsKey(variable)){
                    Set<AbstractObject> set = new HashSet<AbstractObject>();
                    store.get(scope).put(variable, set);
                    changed = true;
                }
                break;
            }
        }
        // put the abstract object in the variable store
        changed |= store.get(scope).get(variable).addAll(objs);
    }

    public Set<AbstractObject> readStore(String variable, Function scope){
        // switch to the outer scope until the variable is defined
        while(! store.get(scope).containsKey(variable)){
            scope = scope.getOuterFunction();
            if(scope == null){
                return new HashSet<AbstractObject>();
            }
        }
        return store.get(scope).get(variable);
    }

    /** adds prop to the propertystore of obj and property */
    public void writePropertyStore(AbstractObject obj, String property, AbstractObject prop){
        if(prop == null){
            return;
        }
        if(! propertystore.containsKey(new Tuple(obj, property))){
            Set<AbstractObject> s = new HashSet<>();
            s.add(prop);
            propertystore.put(new Tuple(obj, property), s);
            changed = true;
        } else {
            changed |= propertystore.get(new Tuple(obj, property)).add(prop);
        }
    }
    /** adds prop to all properties of obj*/
    public void writeAllPropertyStore(AbstractObject obj, AbstractObject prop){
        if(prop == null){
            return;
        }
        for(Tuple t:propertystore.keySet()){
            if(t.a.equals(obj)){
                changed |= propertystore.get(t).add(prop);
            }
        }
    }

    /** adds prop to all properties of obj*/
    public void writeAllPropertyStore(AbstractObject obj, Set<AbstractObject> prop){
        if(prop == null){
            return;
        }
        for(Tuple t:propertystore.keySet()){
            if(t.a.equals(obj)){
                changed |= propertystore.get(t).addAll(prop);
            }
        }
    }

    /** adds prop to all properties of obj*/
    public void writeAllPropertyStore(Set<AbstractObject> objs, Set<AbstractObject> prop){
        if(prop == null || objs == null){
            return;
        }
        for(AbstractObject obj: objs) {
            for (Tuple t : propertystore.keySet()) {
                if (t.a.equals(obj)) {
                    changed |= propertystore.get(t).addAll(prop);
                }
            }
        }
    }

    /** adds prop to the propertystore of all objects in objs and property */
    public void writePropertyStore(Set<AbstractObject> objs, String property, Set<AbstractObject> prop){
        if(prop == null || prop.isEmpty() || objs == null) {
            return;
        }
        for(AbstractObject obj: objs) {
            if (!propertystore.containsKey(new Tuple(obj, property))) {
                Set<AbstractObject> s = new HashSet<>();
                s.addAll(prop);
                propertystore.put(new Tuple(obj, property), s);
                changed = true;
            } else {
                changed |= propertystore.get(new Tuple(obj, property)).addAll(prop);
            }
        }
    }
    /** adds prop to all properties in property of all AbstractObjects in objs*/
    public void writePropertyStore(Set<AbstractObject> objs, Set<String> property, Set<AbstractObject> prop){
        if(property == null || property.isEmpty() || objs == null || prop == null ){
            return;
        }
        for(AbstractObject obj: objs){
            for(String propName: property){
                if(!propertystore.containsKey(new Tuple(obj, propName))){
                    Set<AbstractObject> s = new HashSet<AbstractObject>();
                    s.addAll(prop);
                    propertystore.put(new Tuple(obj, propName), s);
                    changed = true;
                } else {
                    changed |= propertystore.get(new Tuple(obj, propName)).addAll(prop);
                }
            }
        }
    }
    /** reads the property with the same name as the argument of all abstractObjects in objs*/
    public Set<AbstractObject> readPropertyStore(Set<AbstractObject> objs, String property){
        if(objs == null){
            return new HashSet<AbstractObject>();
        }
        Set<AbstractObject> res = new HashSet<AbstractObject>();
        for(AbstractObject obj: objs){
            if(propertystore.containsKey(new Tuple(obj, property))){
                res.addAll(propertystore.get(new Tuple(obj,property)));
            }
        }
        return res;
    }

    /** reads the property with the same name as the argument of all abstractObjects in objs*/
    public Set<AbstractObject> readPropertyStore(Set<AbstractObject> objs, Set<String> property){
        Set<AbstractObject> res = new HashSet<AbstractObject>();
        if (objs == null || property == null){
            return new HashSet<AbstractObject>();
        }
        for(AbstractObject obj: objs){
            for(String prop: property){
                if(propertystore.containsKey(new Tuple(obj, prop))){
                    res.addAll(propertystore.get(new Tuple(obj,prop)));
                }
            }
        }
        return res;
    }

    /** returns the abstractObjects of all properties of all the abstractObjects in objs*/
    public Set<AbstractObject> readAllPropertyStore(Set<AbstractObject> objs){
        if(objs == null){
            return new HashSet<AbstractObject>();
        }
        Set<AbstractObject> res = new HashSet<AbstractObject>();
        for(Tuple tuple: propertystore.keySet()){
            if(objs.contains(tuple.a)){
                res.addAll(propertystore.get(tuple));
            }
        }
        return res;
    }

    public Set<AbstractObject> getPropertyNames(Set<AbstractObject> objs, AbstractNode node){
        Set<AbstractObject> res = new HashSet<AbstractObject>();
        for(Tuple tup: propertystore.keySet()){
            if(objs.contains(tup.a)){
                res.add(new AbstractObject(node, tup.b));
            }
        }
        return res;
    }
}
