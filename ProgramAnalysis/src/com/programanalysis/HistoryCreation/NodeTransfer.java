package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.AbstractObject;
import com.programanalysis.PointerAnalysis.BlockRegisters;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.flowgraph.jsnodes.*;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.solver.NodeAndContext;
import dk.brics.tajs.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cedri on 5/9/2016.
 */
public class NodeTransfer implements NodeVisitor {

    private PointerAnalysis pointerAnalysis;

    private HistoryCreation historyCreation;

    public NodeTransfer(PointerAnalysis pointerAnalysis, HistoryCreation historyCreation){
        this.pointerAnalysis = pointerAnalysis;
        this.historyCreation = historyCreation;
    }

    @Override
    public void visit(AssumeNode assumeNode, Object o) {

    }

    @Override
    public void visit(BinaryOperatorNode binaryOperatorNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(binaryOperatorNode.getBlock());
        reg.writeRegister(binaryOperatorNode.getResultRegister(), new HashSet<AbstractObject>());
    }

    @Override
    public void visit(CallNode callNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(callNode.getBlock());
        Object obj = reg.readRegister(callNode.getBaseRegister());
        Function f = null;
        //TODO: any way to make this less ugly and faster?
        Map<BlockAndContext, Set<Pair>> callSources = pointerAnalysis.getCallgraph().getCallSources();
        for(Set<Pair> entry: callSources.values()){
            for(Pair pair: entry){
                if(((NodeAndContext)(pair.getFirst())).getNode().equals(callNode)){
                    for(BlockAndContext block: callSources.keySet()){
                        if(callSources.get(block).equals(entry)){
                            // we have found the function that we want to call
                            f = block.getBlock().getFunction();
                        }
                    }
                }
            }
        }

        if(f == null){
            f = pointerAnalysis.getCallGraphParser().getFunction(callNode.getSourceLocation().getLineNumber(), callNode.getSourceLocation().getColumnNumber());
        }

        Map<AbstractObject, History> state = historyCreation.getState().getCurrentState();
        if(f == null){
            if(callNode.getPropertyString()!= null && (callNode.getPropertyString().equals("call") || callNode.getPropertyString().equals("apply"))){
                // we ignore these in the history
                return;
            }
            // we don't have the source code for the function
            if (obj != null) {
                // add the current api call to the histories
                for (AbstractObject absObj : (Set<AbstractObject>) obj) {
                    if (!state.keySet().contains(absObj)) {
                        History h = new History(absObj);
                        state.put(absObj, h);
                    }
                    if(callNode.getPropertyString() != null) {
                        state.get(absObj).add(new APICallTuple(callNode, callNode.getPropertyString()));
                    } else {
                        //TODO: can we get here?
                    }
                }
            }
            // add a return value to the return register
            Set<AbstractObject> set = new HashSet<AbstractObject>();
            set.add(new AbstractObject(callNode));
            reg.writeRegister(callNode.getResultRegister(), set);
        } else {
            if (obj != null) {
                // add the current api call to the histories
                for (AbstractObject absObj : (Set<AbstractObject>) obj) {
                    if (!state.keySet().contains(absObj)) {
                        History h = new History(absObj);
                        state.put(absObj, h);
                    }
                    if(callNode.getPropertyString() != null){
                        state.get(absObj).add(new APICallTuple(callNode, callNode.getPropertyString()));
                    } else {
                        if(f.getName()==null){
                            //we call a function variable, therefore it's not an API call and not of our interest
                        } else {
                            state.get(absObj).add(new APICallTuple(callNode, f.getName()));
                        }
                    }
                }
            }
            Map<AbstractObject, History> outState = historyCreation.getOldState().getFunctionOutState(f);
            for (AbstractObject absObj : outState.keySet()) {
                if (state.keySet().contains(absObj)) {
                    state.get(absObj).add(outState.get(absObj));
                } else {
                    state.put(absObj, outState.get(absObj).copy());
                }
            }

            // add the return value to the return register
            Object returnValue = pointerAnalysis.getState().getOutstate(f).returnObjects;
            reg.writeRegister(callNode.getResultRegister(), returnValue);
        }
    }

    @Override
    public void visit(CatchNode catchNode, Object o) {

    }

    @Override
    public void visit(ConstantNode constantNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(constantNode.getBlock());
        if(constantNode.getType().equals(ConstantNode.Type.STRING)){
            AbstractObject abs = new AbstractObject(constantNode, constantNode.getString());
            Set<AbstractObject> set = new HashSet<AbstractObject>();
            set.add(abs);
            reg.writeRegister(constantNode.getResultRegister(), set);
        } else if(constantNode.getType().equals(ConstantNode.Type.NUMBER)){
            String number = (new Integer((new Double(constantNode.getNumber()).intValue()))).toString();
            AbstractObject obj = new AbstractObject(constantNode, number);
            HashSet<AbstractObject> s = new HashSet<>();
            s.add(obj);
            reg.writeRegister(constantNode.getResultRegister(), s);
        } else {
            reg.writeRegister(constantNode.getResultRegister(), new HashSet<AbstractObject>());
        }
    }

    @Override
    public void visit(DeletePropertyNode deletePropertyNode, Object o) {

    }

    @Override
    public void visit(BeginWithNode beginWithNode, Object o) {

    }

    @Override
    public void visit(ExceptionalReturnNode exceptionalReturnNode, Object o) {

    }

    @Override
    public void visit(DeclareFunctionNode declareFunctionNode, Object o) {

    }

    @Override
    public void visit(BeginForInNode beginForInNode, Object o) {

    }

    @Override
    public void visit(IfNode ifNode, Object o) {

    }

    @Override
    public void visit(EndWithNode endWithNode, Object o) {

    }

    @Override
    public void visit(NewObjectNode newObjectNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(newObjectNode.getBlock());
        AbstractObject absobj = new AbstractObject(newObjectNode);
        Set<AbstractObject> set = new HashSet<AbstractObject>();
        set.add(absobj);
        reg.writeRegister(newObjectNode.getResultRegister(), set);
    }

    @Override
    public void visit(NextPropertyNode nextPropertyNode, Object o) {

    }

    @Override
    public void visit(HasNextPropertyNode hasNextPropertyNode, Object o) {

    }

    @Override
    public void visit(NopNode nopNode, Object o) {

    }

    @Override
    public void visit(ReadPropertyNode readPropertyNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(readPropertyNode.getBlock());
        Object base = reg.readRegister(readPropertyNode.getBaseRegister());
        if(readPropertyNode.isPropertyFixed()){
            reg.writeRegister(readPropertyNode.getResultRegister(), pointerAnalysis.getState().readPropertyStore((Set<AbstractObject>)base, readPropertyNode.getPropertyString()));
        } else {
            Object prop = reg.readRegister(readPropertyNode.getPropertyRegister());
            if(prop != null && prop instanceof Set && !((Set)prop).isEmpty()){
                Set<AbstractObject> propSet = (Set<AbstractObject>) prop;
                Set<String> propertyNames = new HashSet<String>();
                for(AbstractObject absObj: propSet){
                    if(absObj.getStringValue() != null){
                        propertyNames.add(absObj.getStringValue());
                    }
                }
                reg.writeRegister(readPropertyNode.getResultRegister(), pointerAnalysis.getState().readPropertyStore((Set<AbstractObject>)base, propertyNames));
            } else {
                // we don't know which property, so we read all
                reg.writeRegister(readPropertyNode.getResultRegister(), pointerAnalysis.getState().readAllPropertyStore((Set<AbstractObject>)base));
            }
        }
    }

    @Override
    public void visit(ReadVariableNode readVariableNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(readVariableNode.getBlock());
        String variable = readVariableNode.getVariableName();
        if(variable.equals("this")){
            Object obj = pointerAnalysis.getState().getInstate(readVariableNode.getBlock().getFunction()).thisObjects;
            reg.writeRegister(readVariableNode.getResultRegister(), obj);
        } else {
            if(readVariableNode.getBlock().getFunction().getParameterNames().contains(variable)){
                // we want to read a function argument
                Object obj = pointerAnalysis.getState().getInstate(readVariableNode.getBlock().getFunction()).argumentObjects.get(variable);
                reg.writeRegister(readVariableNode.getResultRegister(), obj);
            } else {
                Object obj = pointerAnalysis.getState().readStore(variable, readVariableNode.getBlock().getFunction());
                reg.writeRegister(readVariableNode.getResultRegister(), obj);
                //TODO: what is the ResultBaseRegister?
            }
        }
    }

    @Override
    public void visit(ReturnNode returnNode, Object o) {

    }

    @Override
    public void visit(ThrowNode throwNode, Object o) {

    }

    @Override
    public void visit(TypeofNode typeofNode, Object o) {

    }

    @Override
    public void visit(UnaryOperatorNode unaryOperatorNode, Object o) {
        BlockRegisters reg = historyCreation.getState().getRegisters(unaryOperatorNode.getBlock());
        Set<AbstractObject> set = new HashSet<AbstractObject>();
        reg.writeRegister(unaryOperatorNode.getResultRegister(), set);
    }

    @Override
    public void visit(DeclareVariableNode declareVariableNode, Object o) {

    }

    @Override
    public void visit(WritePropertyNode writePropertyNode, Object o) {

    }

    @Override
    public void visit(WriteVariableNode writeVariableNode, Object o) {

    }

    @Override
    public void visit(EventDispatcherNode eventDispatcherNode, Object o) {

    }

    @Override
    public void visit(EndForInNode endForInNode, Object o) {

    }

    @Override
    public void visit(BeginLoopNode beginLoopNode, Object o) {

    }

    @Override
    public void visit(EndLoopNode endLoopNode, Object o) {

    }
}
