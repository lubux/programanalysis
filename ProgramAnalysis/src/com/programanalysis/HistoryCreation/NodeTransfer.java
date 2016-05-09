package com.programanalysis.HistoryCreation;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.flowgraph.jsnodes.*;

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
