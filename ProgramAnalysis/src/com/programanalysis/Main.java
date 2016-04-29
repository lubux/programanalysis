package com.programanalysis;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import dk.brics.tajs.analysis.Analysis;

public class Main {

    public static void main(String[] args) {
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(args, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        PointerAnalysis analysis = new PointerAnalysis(tajsAnalysis);
        analysis.init();
        analysis.solve();
    }
}
