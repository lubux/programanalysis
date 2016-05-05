package com.programanalysis.test.analysis;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.FileUtil;
import com.programanalysis.util.CallGraphCaller;
import com.programanalysis.util.CallGraphParser;
import dk.brics.tajs.analysis.Analysis;
import org.junit.Test;

import java.io.File;

import java.io.IOException;

/**
 * Created by lukas on 05.05.16.
 */
public class TestAnalysis {

    //TODO:
    @Test
    public void simpleAnalysisDebug() {

    }

    @Test
    public void simpleAnalysisDebug2() throws IOException {
        String filePath = FileUtil.makePath("data", "javascriptfiles", "test_javascript.js");
        // run the tajs analysis
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(new String[] {filePath}, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        // get the full call graph
        String cg = CallGraphCaller.getCallGraph(FileUtil.getNodeJSCallGraphCMD(filePath));
        CallGraphParser cgp = null;
        try {
            cgp = new CallGraphParser(cg, filePath, tajsAnalysis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PointerAnalysis analysis = new PointerAnalysis(tajsAnalysis, cgp);
        analysis.init();
        analysis.solve();
        System.out.print("Pointer Analysis completed");
    }


}
