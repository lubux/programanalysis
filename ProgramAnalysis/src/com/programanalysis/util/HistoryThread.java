package com.programanalysis.util;

import com.programanalysis.HistoryCreation.HistoryCreation;
import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.analysis.Analysis;

/**
 * Created by cedri on 5/17/2016.
 */
public class HistoryThread implements Runnable {

    public HistoryCreation hist;

    private Analysis tajsAnalysis;

    private CallGraphParser cgp;

    public HistoryThread(Analysis tajsAnalysis, CallGraphParser cgp){
        this.tajsAnalysis = tajsAnalysis;
        this.cgp = cgp;
    }

    @Override
    public void run() {
        PointerAnalysis pointerAnalysis = new PointerAnalysis(tajsAnalysis, cgp, false, -1, -1);
        pointerAnalysis.init();
        pointerAnalysis.solve();
        hist = new HistoryCreation(tajsAnalysis, pointerAnalysis);
        hist.solve();
    }
}
