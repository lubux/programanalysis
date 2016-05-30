package com.programanalysis.util;

import dk.brics.tajs.analysis.Analysis;

/**
 * Created by cedri on 5/16/2016.
 */
public class tajsThread implements Runnable {

    private String filePath;

    public Analysis tajsAnalysis;

    public boolean error=false;


    public tajsThread(String filePath){
        this.filePath = filePath;
    }
    @Override
    public void run() {
        try{
            tajsAnalysis = dk.brics.tajs.Main.init(new String[] {filePath, "-dom"}, null);
            dk.brics.tajs.Main.run(tajsAnalysis);
        } catch(Exception e){
            dk.brics.tajs.Main.reset();
            error = true;
        }
    }
}
