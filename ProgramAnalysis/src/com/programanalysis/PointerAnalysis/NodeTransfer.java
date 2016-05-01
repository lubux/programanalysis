package com.programanalysis.PointerAnalysis;

import dk.brics.tajs.flowgraph.Function;
import dk.brics.tajs.flowgraph.jsnodes.*;
import dk.brics.tajs.solver.BlockAndContext;
import dk.brics.tajs.solver.NodeAndContext;
import dk.brics.tajs.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cedri on 4/28/2016.
 */
//todo: copy the transfer functions from the other NodeTransfer class
public class NodeTransfer implements NodeVisitor {

    PointerAnalysis analysis;

    public NodeTransfer(PointerAnalysis analysis){
        this.analysis = analysis;
    }

    @Override
    public void visit(AssumeNode assumeNode, Object o) {

    }

    @Override
    public void visit(BinaryOperatorNode binaryOperatorNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(binaryOperatorNode.getBlock());
        registers.writeRegister(binaryOperatorNode.getResultRegister(), new HashSet<AbstractObject>());
        //TODO: maybe support string concatenation here?

        /*BlockRegisters registers = analysis.getRegisters(binaryOperatorNode.getBlock());
        Object arg1 = registers.readRegister(binaryOperatorNode.getArg1Register());
        Object arg2 = registers.readRegister(binaryOperatorNode.getArg2Register());
        // only handle string concatenation for the property access
        if(arg1 != null && arg1 instanceof String && arg2 != null && arg2 instanceof String){
            if(binaryOperatorNode.getOperator().equals(BinaryOperatorNode.Op.ADD)){
                String sol = (String) arg1 + (String) arg2;
                registers.writeRegister(binaryOperatorNode.getResultRegister(), sol);
            }
        }*/
    }

    @Override
    public void visit(CallNode callNode, Object o) {
        Map<BlockAndContext, Set<Pair>> callSources = analysis.getCallGraph().getCallSources();
        BlockRegisters registers = analysis.getRegisters(callNode.getBlock());
        Function callee = null;
        //TODO: any way to make this less ugly and faster?
        for(Set<Pair> entry: callSources.values()){
            for(Pair pair: entry){
                if(((NodeAndContext)(pair.getFirst())).getNode().equals(callNode)){
                    for(BlockAndContext block: callSources.keySet()){
                        if(callSources.get(block).equals(entry)){
                            // we have found the function that we want to call
                            callee = block.getBlock().getFunction();
                        }
                    }
                }
            }
        }
        if(callee == null){
            // if callee is null, we don't have the source code for this function and just create a new abstract object as the return value
            registers.writeRegister(callNode.getResultRegister(), new AbstractObject(callNode));
        } else {
           for(int i = 0; i < callNode.getNumberOfArgs(); i++){
               // add all arguments to the in set of the function
               Object a = registers.readRegister(callNode.getArgRegister(i));
               analysis.getState().addArgToInState((Set<AbstractObject>)a,callee,callee.getParameterNames().get(i));
           }
            if(! callNode.isConstructorCall()) {
                Object obj = registers.readRegister(callNode.getBaseRegister());
                analysis.getState().addThisToInState((Set)obj, callee);
                registers.writeRegister(callNode.getResultRegister(), analysis.getState().getOutstate(callee).returnObjects);
            } else {
                AbstractObject obj = new AbstractObject(callNode);
                Set<AbstractObject> s = new HashSet<AbstractObject>();
                s.add(obj);
                analysis.getState().addThisToInState(s, callee);
                registers.writeRegister(callNode.getResultRegister(), s);
            }
            analysis.addToWorklist(callee);
        }
    }

    @Override
    public void visit(CatchNode catchNode, Object o) {

    }

    @Override
    public void visit(ConstantNode constantNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(constantNode.getBlock());
        if(constantNode.getType().equals(ConstantNode.Type.STRING)){
            // simply return a string that can be used for property access
            AbstractObject obj = new AbstractObject(constantNode, constantNode.getString());
            HashSet<AbstractObject> s = new HashSet<>();
            s.add(obj);
            registers.writeRegister(constantNode.getResultRegister(), s);
        } else {
            registers.writeRegister(constantNode.getResultRegister(), new HashSet<AbstractObject>());
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

    }

    @Override
    public void visit(NextPropertyNode nextPropertyNode, Object o) {

    }

    @Override
    public void visit(HasNextPropertyNode hasNextPropertyNode, Object o) {

    }

    @Override
    public void visit(NopNode nopNode, Object o) {
        // do nothing
    }

    @Override
    public void visit(ReadPropertyNode readPropertyNode, Object o) {
        //TODO: what if base is null?
        BlockRegisters registers = analysis.getRegisters(readPropertyNode.getBlock());
        Object base = registers.readRegister(readPropertyNode.getBaseRegister());
        if(readPropertyNode.isPropertyFixed()){
            String property = readPropertyNode.getPropertyString();
            Set<AbstractObject> objs = analysis.getState().readPropertyStore((Set<AbstractObject>)base,property);
            registers.writeRegister(readPropertyNode.getResultRegister(), objs);
        } else {
            Object prop = registers.readRegister(readPropertyNode.getPropertyRegister());
            if(prop != null && prop instanceof Set && !((Set)prop).isEmpty()){
                Set<AbstractObject> propSet = (Set<AbstractObject>) prop;
                Set<String> propertyNames = new HashSet<String>();
                for(AbstractObject absObj: propSet){
                    if(absObj.getStringValue() != null){
                        propertyNames.add(absObj.getStringValue());
                    }
                }
                registers.writeRegister(readPropertyNode.getResultRegister(), analysis.getState().readPropertyStore((Set<AbstractObject>)base, propertyNames));
            } else {
                // we don't know which property, so we read all
                registers.writeRegister(readPropertyNode.getResultRegister(), analysis.getState().readAllPropertyStore((Set<AbstractObject>)base));
            }
        }
    }

    @Override
    public void visit(ReadVariableNode readVariableNode, Object o) {
        String variable = readVariableNode.getVariableName();
        BlockRegisters registers = analysis.getRegisters(readVariableNode.getBlock());
        if(variable.equals("this")){
            Object obj = analysis.getState().getInstate(readVariableNode.getBlock().getFunction()).thisObjects;
            registers.writeRegister(readVariableNode.getResultRegister(), obj);
        } else {
            if(readVariableNode.getBlock().getFunction().getParameterNames().contains(variable)){
                // we want to read a function argument
                Object obj = analysis.getState().getInstate(readVariableNode.getBlock().getFunction()).argumentObjects.get(variable);
                registers.writeRegister(readVariableNode.getResultRegister(), obj);
            } else {
                Object obj = analysis.getState().readStore(variable, readVariableNode.getBlock().getFunction());
                registers.writeRegister(readVariableNode.getResultRegister(), obj);
            //TODO: what is the ResultBaseRegister?
            }
        }
    }

    @Override
    public void visit(ReturnNode returnNode, Object o) {
        // add the set of objects to the OutState of the function
        BlockRegisters registers = analysis.getRegisters(returnNode.getBlock());
        Object obj = registers.readRegister(returnNode.getReturnValueRegister());
        if(obj != null && obj instanceof Set){
            analysis.getState().addToOutState((Set<AbstractObject>) obj, returnNode.getBlock().getFunction());
        }
    }

    @Override
    public void visit(ThrowNode throwNode, Object o) {

    }

    @Override
    public void visit(TypeofNode typeofNode, Object o) {

    }

    @Override
    public void visit(UnaryOperatorNode unaryOperatorNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(unaryOperatorNode.getBlock());
        registers.writeRegister(unaryOperatorNode.getResultRegister(), new HashSet<AbstractObject>());
    }

    @Override
    public void visit(DeclareVariableNode declareVariableNode, Object o) {
        String name = declareVariableNode.getVariableName();
        analysis.getState().variableDeclaration(declareVariableNode.getVariableName(), declareVariableNode.getBlock().getFunction());
    }

    @Override
    public void visit(WritePropertyNode writePropertyNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(writePropertyNode.getBlock());
        Object base = registers.readRegister(writePropertyNode.getBaseRegister());
        Object value = registers.readRegister(writePropertyNode.getValueRegister());
        String propertyName = null;
        if(writePropertyNode.isPropertyFixed()){
            propertyName = writePropertyNode.getPropertyString();
            analysis.getState().writePropertyStore((Set)base, propertyName, (Set) value);
        } else {
            Object prop = registers.readRegister(writePropertyNode.getPropertyRegister());
            if(prop instanceof  Set){
                Set<String> propName = new HashSet<String>();
                for(AbstractObject absobj: (Set<AbstractObject>)prop){
                    if(absobj.getStringValue() != null){
                        propName.add(absobj.getStringValue());
                    }
                }
                analysis.getState().writePropertyStore((Set) base, propName, (Set) value);
            } else {
                // we don't know what property is written because we don't know the values of any variable
                //so we assign every property
                analysis.getState().writeAllPropertyStore((Set)base, (Set)value);
            }
        }
    }

    @Override
    public void visit(WriteVariableNode writeVariableNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(writeVariableNode.getBlock());
        Object obj = registers.readRegister(writeVariableNode.getValueRegister());
        //TODO: what if variable is function argument? (see tajs)
        // if we assign an abstract object, just add it to the store set of the variable
        if(obj instanceof Set){
            analysis.getState().writeStore(writeVariableNode.getVariableName(), writeVariableNode.getBlock().getFunction(), (Set) obj);
        } else {
            // TODO: can we even get here?
        }
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
