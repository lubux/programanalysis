package com.programanalysis;

import com.programanalysis.HistoryCreation.History;
import com.programanalysis.HistoryCreation.HistoryCreation;
import com.programanalysis.PointerAnalysis.AbstractObject;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.CallGraphCaller;
import com.programanalysis.util.CallGraphParser;
import com.programanalysis.util.FileUtil;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.AbstractNode;

import java.io.IOException;
import java.util.Set;

/**
 * Created by cedri on 5/16/2016.
 */
public class HistoryExtraction {

    private int lineNumber;

    private int columnNumber;

    private String sourcePath;

    private String variableName;

    private String extractedHistories;

    public HistoryExtraction(String sourcePath, int lineNumber, int columnNumber, String variableName, int progId, int nodeId) throws IOException {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.sourcePath = sourcePath;
        this.variableName = variableName;
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(new String[] {sourcePath, "-dom"}, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        String cg = CallGraphCaller.getCallGraph(FileUtil.getNodeJSCallGraphCMD(sourcePath));
        CallGraphParser cgp = new CallGraphParser(cg, sourcePath, tajsAnalysis);
        PointerAnalysis pointerAnalysis = new PointerAnalysis(tajsAnalysis, cgp, true, lineNumber, columnNumber);
        pointerAnalysis.init();
        pointerAnalysis.solve();
        Set<AbstractNode> nodeSet = pointerAnalysis.getNodesOfInterest();
        HistoryCreation hist = new HistoryCreation(tajsAnalysis, pointerAnalysis, variableName, nodeSet);
        hist.solve();
        extractedHistories = hist.printExtractionHistories(progId, nodeId);
        dk.brics.tajs.Main.reset();
    }

    public String getExtractedHistories(){
        return extractedHistories;
    }
}
