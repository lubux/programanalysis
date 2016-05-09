package com.programanalysis.test.analysis;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.FileUtil;
import com.programanalysis.util.CallGraphCaller;
import com.programanalysis.util.CallGraphParser;
import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.util.AnalysisException;
import org.junit.Test;

import java.io.*;

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
        String filePath = "data"+ File.separator +"javascriptfiles"+File.separator+ "test_javascript.js";
        //String filePath2 = "C:\\PA_Programs\\data\\j03m\\trafficcone\\public\\assets\\hero\\hero.js";

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

    @Test
    public void simpleAnalysisDebug3() throws IOException {
        String basePath = "C:\\PA_Programs\\";
        File pathFile = new File(basePath + "programs_training.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(pathFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String filePath = basePath + line;
                File sourceFile = new File(filePath);
                if(! sourceFile.exists()){
                    continue;
                }
                // run the tajs analysis
                Analysis tajsAnalysis = null;
                try{
                    tajsAnalysis = dk.brics.tajs.Main.init(new String[] {filePath}, null);
                    dk.brics.tajs.Main.run(tajsAnalysis);
                } catch(Exception e){
                    dk.brics.tajs.Main.reset();
                    continue;
                }

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
                System.out.println("Pointer Analysis completed for file: " + line);
            }
        }
    }


}
