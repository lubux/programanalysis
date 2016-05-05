package com.programanalysis;

import com.programanalysis.PointerAnalysis.PointerAnalysis;
import com.programanalysis.util.CallGraphCaller;
import com.programanalysis.util.CallGraphParser;
import dk.brics.tajs.analysis.Analysis;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static java.lang.Runtime.getRuntime;

public class Main {

    public static void main(String[] args) {
        // run the tajs analysis
        Analysis tajsAnalysis = dk.brics.tajs.Main.init(args, null);
        dk.brics.tajs.Main.run(tajsAnalysis);
        // get the full call graph
        //TODO: this has to be changed
        String cg = CallGraphCaller.getCallGraph("node C:\\Users\\cedri\\Desktop\\javascript-call-graph-master\\main.js --cg  " + args[0]);
        CallGraphParser cgp = null;
        try {
            cgp = new CallGraphParser(cg, args[0], tajsAnalysis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PointerAnalysis analysis = new PointerAnalysis(tajsAnalysis, cgp);
        analysis.init();
        analysis.solve();
        System.out.print("Pointer Analysis completed");

    }
}
