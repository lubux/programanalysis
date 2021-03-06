package com.programanalysis.test.analysis;

import com.programanalysis.HistoryCreation.HistoryCreation;
import com.programanalysis.HistoryExtraction;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.PredictionHistory;
import com.programanalysis.util.*;
import dk.brics.tajs.analysis.Analysis;
import org.junit.Test;

import java.io.*;
import java.util.Timer;

/**
 * Created by lukas on 05.05.16.
 */
public class TestAnalysis {
    
    @Test
    public void simpleAnalysisDebug() {

    }

    @Test
    public void simpleAnalysisDebug2() throws IOException {
        String filePath = "data"+ File.separator +"javascriptfiles"+File.separator+ "implementation_tests/looptest5.js";

        // run the tajs analysis
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(new String[] {filePath, "-dom"}, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        // get the full call graph
        String cg = CallGraphCaller.getCallGraph(FileUtil.getNodeJSCallGraphCMD(filePath));
        CallGraphParser cgp = null;
        try {
            cgp = new CallGraphParser(cg, filePath, tajsAnalysis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PointerAnalysis analysis = new PointerAnalysis(tajsAnalysis, cgp, false, -1, -1);
        analysis.init();
        analysis.solve();
        //System.out.println("Pointer Analysis completed");
        HistoryCreation hist = new HistoryCreation(tajsAnalysis, analysis);
        hist.solve();
        //System.out.println("History creation completed");
        String result = hist.printSelectedHistories();
        System.out.print(result);
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
                    tajsAnalysis = dk.brics.tajs.Main.init(new String[] {filePath, "-dom"}, null);
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
                } catch (Exception e) {
                    dk.brics.tajs.Main.reset();
                    continue;
                }
                PointerAnalysis pointerAnalysis = new PointerAnalysis(tajsAnalysis, cgp, false, -1, -1);
                pointerAnalysis.init();
                pointerAnalysis.solve();
                //System.out.println("Pointer Analysis completed for file: " + line);
                HistoryCreation hist = new HistoryCreation(tajsAnalysis, pointerAnalysis);
                hist.solve();
                //System.out.println("History creation completed");
                System.out.print(hist.printSelectedHistories());

                // reset tajs so the flowgraph will be deleted
                dk.brics.tajs.Main.reset();
            }
        }

    }

    @Test
    public void predictionHistory() throws IOException {
        String filePath = "data"+ File.separator +"javascriptfiles"+File.separator+ "tempTest.js";
        PredictionHistory predHist = new PredictionHistory(filePath, 7, 10);
        String sol = predHist.getPredictionHistories();
        System.out.print(sol);
    }

    @Test
    public void HistoryExtraction() throws IOException {
        String filePath = "data"+ File.separator +"javascriptfiles"+File.separator+ "";
        HistoryExtraction histExt = new HistoryExtraction(filePath,40,5,"beverage3", 0,0);
        String sol = histExt.getExtractedHistories();
        System.out.print(sol);
    }

    @Test
    public void trainingSetCreation() throws IOException {
        boolean skip = true;
        int counter = 0;
        int generalCounter = 0;
        String basePath = "C:\\PA_Programs\\";
        File pathFile = new File(basePath + "programs_eval.txt");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(basePath + "trainingSet.txt"), "utf-8"));

        try (BufferedReader br = new BufferedReader(new FileReader(pathFile))) {
            String line;
            while ((line = br.readLine()) != null && counter < 100000) {
                generalCounter++;
                System.out.println(generalCounter + " " + counter + " " + line);
                String filePath = basePath + line;
                File sourceFile = new File(filePath);
                if(! sourceFile.exists()){
                    continue;
                }
                // run the tajs analysis
                Analysis tajsAnalysis = null;

                // create a timer for timeout, set timeout to 8 seconds
                Timer timer = new Timer(true);
                InterruptTimerTask interruptTimerTask =
                        new InterruptTimerTask(Thread.currentThread());
                timer.schedule(interruptTimerTask, 8000);
                tajsThread tajsT = new tajsThread(filePath);
                Thread t = new Thread(tajsT);
                try{
                    t.start();
                    t.join();
                    tajsAnalysis = tajsT.tajsAnalysis;
                } catch(Exception e){
                    t.stop();
                    dk.brics.tajs.Main.reset();
                    System.out.println("Tajs timeout: " + filePath);
                    continue;
                } finally{
                    timer.cancel();
                }
                if(tajsAnalysis == null){
                    continue;
                }
                if(tajsT.error){
                    continue;
                }

                // get the full call graph
                String cg = CallGraphCaller.getCallGraph(FileUtil.getNodeJSCallGraphCMD(filePath));
                CallGraphParser cgp = null;
                try {
                    cgp = new CallGraphParser(cg, filePath, tajsAnalysis);
                } catch (Exception e) {
                    dk.brics.tajs.Main.reset();
                    continue;
                }
                // create a new thread for the history creation, limit the pointer analysis & history creation to 16 seconds
                timer = new Timer(true);
                interruptTimerTask =
                        new InterruptTimerTask(Thread.currentThread());
                timer.schedule(interruptTimerTask, 16000);
                HistoryThread histT = new HistoryThread(tajsAnalysis, cgp);
                t = new Thread(histT);
                HistoryCreation hist;
                try {
                    t.start();
                    t.join();
                    hist = histT.hist;
                }catch(Exception e){
                    t.stop();
                    dk.brics.tajs.Main.reset();
                    System.out.println("Our timeout: " + filePath);
                    continue;
                } finally{
                    timer.cancel();
                }
                if(hist != null) {
                    String histories = hist.printSelectedHistories();
                    if (!histories.equals("")) {
                        counter++;
                        writer.write(line + "\n");
                        writer.write(histories);
                    }
                }
                // reset tajs so the flowgraph will be deleted
                dk.brics.tajs.Main.reset();
            }
        }
        writer.close();
    }
}
