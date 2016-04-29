package com.programanalysis.PointerAnalysis;

import com.programanalysis.lattice.AbstractObject;
import dk.brics.tajs.flowgraph.jsnodes.*;

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
        Object arg1 = registers.readRegister(binaryOperatorNode.getArg1Register());
        Object arg2 = registers.readRegister(binaryOperatorNode.getArg2Register());
        // only handle string concatenation for the property access
        if(arg1 != null && arg1 instanceof String && arg2 != null && arg2 instanceof String){
            if(binaryOperatorNode.getOperator().equals(BinaryOperatorNode.Op.ADD)){
                String sol = (String) arg1 + (String) arg2;
                registers.writeRegister(binaryOperatorNode.getResultRegister(), sol);
            }
        }
    }

    @Override
    public void visit(CallNode callNode, Object o) {

    }

    @Override
    public void visit(CatchNode catchNode, Object o) {

    }

    @Override
    public void visit(ConstantNode constantNode, Object o) {
        if(constantNode.getType().equals(ConstantNode.Type.STRING)){
            BlockRegisters registers = analysis.getRegisters(constantNode.getBlock());
            // simply return a string that can be used for property access
            registers.writeRegister(constantNode.getResultRegister(), constantNode.getString());
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

    }

    @Override
    public void visit(ReadPropertyNode readPropertyNode, Object o) {

    }

    @Override
    public void visit(ReadVariableNode readVariableNode, Object o) {
        String variable = readVariableNode.getVariableName();
        BlockRegisters registers = analysis.getRegisters(readVariableNode.getBlock());
        if(variable.equals("this")){
            //TODO: handle this (use stack of this objects in state?)
        } else {
            Object obj = analysis.getState().readStore(variable, readVariableNode.getBlock().getFunction());
            registers.writeRegister(readVariableNode.getResultRegister(), obj);
            //TODO: what is the ResultBaseRegister?
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
        } else {
            Object prop = registers.readRegister(writePropertyNode.getPropertyRegister());
            if(prop instanceof  String){
                propertyName = (String) prop;
            } else {
                // we don't know what property is written because we don't know the values of any variable
                //so we assign every property
                analysis.getState().writeAllPropertyStore((AbstractObject)base, (AbstractObject)value);

            }
        }
        //TODO: are base and value sets?
        analysis.getState().writePropertyStore((AbstractObject)base, propertyName, (AbstractObject) value);
    }

    @Override
    public void visit(WriteVariableNode writeVariableNode, Object o) {
        BlockRegisters registers = analysis.getRegisters(writeVariableNode.getBlock());
        Object obj = registers.readRegister(writeVariableNode.getValueRegister());
        //TODO: what if variable is function argument? (see tajs)
        // if we assign an abstract object, just add it to the store set of the variable
        if(obj instanceof AbstractObject){
            analysis.getState().writeStore(writeVariableNode.getVariableName(), writeVariableNode.getBlock().getFunction(), (AbstractObject) obj);
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
