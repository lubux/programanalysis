package com.programanalysis.util;

import dk.brics.tajs.analysis.Analysis;
import dk.brics.tajs.flowgraph.Function;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by cedri on 5/4/2016.
 */
public class CallGraphParser {

    // This only works on soruce file with linux line separators -> \n

    private String input;

    private String sourceCode;

    private Analysis analysis;

    private Map<Integer, Map<Integer, Function>> functionMap;

    /** used for the printing of the histories*/
    private Map<Function, Set<Function>> historyCallGraph;

    /**
     * constructor
     *
     * @param input    the text input that was given from the console execution of javascript call graph
     * @param filePath the path to the source file that was processed by javascript call graph
     */
    public CallGraphParser(String input, String filePath, Analysis analysis) throws IOException {
        this.input = input;
        this.analysis = analysis;
        this.functionMap = new HashMap<Integer, Map<Integer, Function>>();
        this.historyCallGraph = new HashMap<Function, Set<Function>>();
        // read in the source code
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = "\n";
        while((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        this.sourceCode = stringBuilder.toString();
        reader.close();
        // build the functionMap
        parse();
    }

    /** adds callee to the set of called functions of caller for the history call graph*/
    public void addFuncToHistCallGraph(Function caller, Function callee){
        if(! historyCallGraph.keySet().contains(caller)){
            Set<Function> set = new HashSet<Function>();
            historyCallGraph.put(caller,set);
        }
        historyCallGraph.get(caller).add(callee);

    }

    /** returns a copy of the historyCallGraph*/
    public Map<Function, Set<Function>> getHistoryCallGraph(){
        Map<Function, Set<Function>> res = new HashMap<Function, Set<Function>>();
        for(Function f: historyCallGraph.keySet()){
            res.put(f, historyCallGraph.get(f));
        }
        return res;
    }

    /** returns the called function by a callnode with the given source location, might return null*/
    public Function getFunction(int srcline, int srccol){
        Map<Integer, Function> map = functionMap.get(srcline);
        if(map == null){
            return null;
        }
        return map.get(srccol);
    }

    private void parse() throws IOException {
        BufferedReader bufReader = new BufferedReader(new StringReader(input));
        String line = null;
        while ((line = bufReader.readLine()) != null) {
            if (StringUtils.countMatches(line, "@") != 2) {
                // we are only interested if the called function is defined in the source code
                continue;
            }
            // extract the line and character numbers of the line
            String src = line.substring(line.indexOf('@') + 1, line.indexOf(' '));
            int srcline = Integer.parseInt(src.substring(0, src.indexOf(':')));
            src = src.substring(src.indexOf(':') + 1);
            int srcchar = Integer.parseInt(src.substring(0, src.indexOf('-')));
            line = line.substring(line.indexOf('@') + 1);
            String dest = line.substring(line.indexOf('@') + 1, line.length());
            int destline = Integer.parseInt(dest.substring(0, dest.indexOf(':')));
            dest = dest.substring(dest.indexOf(':') + 1);
            int destchar = Integer.parseInt(dest.substring(0, dest.indexOf('-')));

            String tmp = sourceCode.substring(0, srcchar);
            int srccol;
            if (tmp.lastIndexOf("\n") < 0) {
                srccol = srcchar;
            } else {
                srccol = srcchar - tmp.lastIndexOf("\n");
            }
            if(srcchar == 0){
                srccol = 1;
            }
            tmp = sourceCode.substring(0, destchar);
            int destcol;
            if (tmp.lastIndexOf("\n") < 0) {
                destcol = destchar;
            } else {
                destcol = destchar - tmp.lastIndexOf("\n");
            }
            if(destchar == 0){
                destcol = 1;
            }
            for (Function f : analysis.getSolver().getFlowGraph().getFunctions()) {
                if (f.getSourceLocation().getLineNumber() == destline && f.getSourceLocation().getColumnNumber() == destcol) {
                    // we have found the right function
                    if (functionMap.keySet().contains(srcline)) {
                        Map<Integer, Function> map = functionMap.get(srcline);
                        map.put(srccol, f);
                    } else {
                        Map<Integer,Function> map = new HashMap<Integer, Function>();
                        map.put(srccol, f);
                        functionMap.put(srcline, map);
                    }
                }
            }
        }

    }
}
