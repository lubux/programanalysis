package com.programanalysis.analysis;

import com.programanalysis.analysis.Solver;
import com.programanalysis.lattice.AbstractObject;
import com.programanalysis.lattice.NullValue;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.*;

/**
 * Created by cedri on 4/24/2016.
 */
//TODO: read registers might be null
public class NodeTransfer implements NodeVisitor {

    private Solver.SolverInterface solver;

    public NodeTransfer(){

    }

    public void setSolverInterface(Solver.SolverInterface solver){
        this.solver = solver;
    }
    @Override
    public void visit(AssumeNode assumeNode, Object o) {

    }

    @Override
    public void visit(BinaryOperatorNode binaryOperatorNode, Object o) {

    }

    @Override
    public void visit(CallNode callNode, Object o) {

    }

    @Override
    public void visit(CatchNode catchNode, Object o) {

    }

    @Override
    public void visit(ConstantNode constantNode, Object o) {

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
        AbstractObject obj = new AbstractObject(newObjectNode);
        if(newObjectNode.getResultRegister() != AbstractNode.NO_VALUE)
            solver.getCurrentState().writeRegister(newObjectNode.getResultRegister(), obj);
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
        if(variable.equals("this")){
            //TODO: handle this (use stack of this objects in state?)
        } else {
            Object obj = solver.getCurrentState().readStore(variable, readVariableNode.getBlock().getFunction());
            solver.getCurrentState().writeRegister(readVariableNode.getResultRegister(), obj);
            //TODO: what is the ResultBaseRegister?
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

    }

    @Override
    public void visit(DeclareVariableNode declareVariableNode, Object o) {
        String name = declareVariableNode.getVariableName();
    }

    @Override
    public void visit(WritePropertyNode writePropertyNode, Object o) {
        Object base = solver.getCurrentState().readRegister(writePropertyNode.getBaseRegister());
        Object value = solver.getCurrentState().readRegister(writePropertyNode.getValueRegister());
        String propertyName = null;
        if(writePropertyNode.isPropertyFixed()){
            propertyName = writePropertyNode.getPropertyString();
        } else {
            Object prop = solver.getCurrentState().readRegister(writePropertyNode.getPropertyRegister());
            if(prop instanceof  String){
                propertyName = (String) prop;
            } else {
                // we don't know what property is written because we don't know the values of any variable
                //so we assign every property

            }
        }
        //TODO: are base and value sets?
        solver.getCurrentState().writePropertyStore((AbstractObject)base, propertyName, (AbstractObject) value);
    }

    @Override
    public void visit(WriteVariableNode writeVariableNode, Object o) {
        Object obj = solver.getCurrentState().readRegister(writeVariableNode.getValueRegister());
        //TODO: what if variable is function argument? (see tajs)
        // if we assign an abstract object, just add it to the store set of the variable
        if(obj instanceof AbstractObject){
            solver.getCurrentState().writeStore(writeVariableNode.getVariableName(), writeVariableNode.getBlock().getFunction(), (AbstractObject) obj);
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
