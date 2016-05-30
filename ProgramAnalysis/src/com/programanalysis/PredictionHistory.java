package com.programanalysis;

import com.programanalysis.HistoryCreation.HistoryCreation;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.CallGraphCaller;
import com.programanalysis.util.CallGraphParser;
import com.programanalysis.util.FileUtil;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;
import dk.brics.tajs.flowgraph.jsnodes.CallNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cedri on 5/16/2016.
 */
public class PredictionHistory {
    private int lineNumber;

    private int columnNumber;

    private String sourcePath;

    private String predictionHistories;

    public static final String predictionFunction = "PADeepLearningUniquePredFunc";

    public PredictionHistory(String sourcePath, int lineNumber, int columnNumber) throws IOException {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.sourcePath = sourcePath;
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(new String[] {sourcePath, "-dom"}, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        String cg = CallGraphCaller.getCallGraph(FileUtil.getNodeJSCallGraphCMD(sourcePath));
        CallGraphParser cgp = new CallGraphParser(cg, sourcePath, tajsAnalysis);
        PointerAnalysis pointerAnalysis = new PointerAnalysis(tajsAnalysis, cgp, true, lineNumber, columnNumber);
        pointerAnalysis.init();
        pointerAnalysis.solve();
        Set<AbstractNode> nodeSet = pointerAnalysis.getNodesOfInterest();
        Set<AbstractNode> newNodeSet = new HashSet<AbstractNode>();
        // filter out the nodes that are not CallNodes and don't call the predictionFunction
        for(AbstractNode node: nodeSet){
            if(node instanceof CallNode){
                CallNode cNode = (CallNode) node;
                if(cNode.getPropertyString()!= null && cNode.getPropertyString().equals(predictionFunction)){
                    newNodeSet.add(node);
                }
            }
        }
        HistoryCreation hist = new HistoryCreation(tajsAnalysis, pointerAnalysis, null, newNodeSet);
        hist.solve();
        predictionHistories = hist.printPredictionHistories();
        dk.brics.tajs.Main.reset();
    }

    public String getPredictionHistories(){
        return predictionHistories;
    }
}
