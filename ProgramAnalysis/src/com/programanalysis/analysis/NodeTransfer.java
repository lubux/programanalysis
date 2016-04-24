package com.programanalysis.analysis;

import com.programanalysis.analysis.Solver;
import com.programanalysis.lattice.AbstractObject;
import dk.brics.tajs.flowgraph.jsnodes.*;

/**
 * Created by cedri on 4/24/2016.
 */
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
        // ignore this values?
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
        solver.getCurrentState().newObject = obj;
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
