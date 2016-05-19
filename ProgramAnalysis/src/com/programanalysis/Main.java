package com.programanalysis;

import com.programanalysis.jsonast.GenProgram;
import com.programanalysis.jsonast.JSONESTree;
import com.programanalysis.jsonast.JSONPrinterCaller;
import com.programanalysis.jsonast.TestFileMarker;
import dk.brics.tajs.flowgraph.SourceLocation;
import org.apache.commons.cli.*;

import com.programanalysis.util.FileUtil;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class Main {

    //main -m[--mode] (data/test_predict/test_hist) -pf[--progfile] -tf[--testfile] -o[--outfile] (path)

    private static final String MODE_DATA = "data";
    private static final String MODE_TEST_PRED= "test_predict";
    private static final String MODE_TEST_HIST= "test_hist";

    private static void help(Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("Main", options);
        System.exit(0);
    }

    private static void reportError(String error) {
        System.err.println(error);
        System.exit(0);
    }

    private static void checkArgsFiles(CommandLine cmd) {
        if(!cmd.hasOption("pf") && cmd.hasOption("tf"))
            reportError("File path arguments missing");
    }


    private static void  parseArgs(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "show help.");
        options.addOption("m", "mode", true, "the mode to execute in (data/test_predict/test_hist)");
        options.addOption("pf", "progfile", true, "the path of the program file, containing the ast's");
        options.addOption("tf", "testfile", true, "the test file defining the test nodes");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption("h")) {
                help(options);
            }

            if(cmd.hasOption("m")) {
                String arg = cmd.getOptionValue("m").toLowerCase();
                if(arg.equals(MODE_DATA)) {
                    //TODO
                } else if(arg.equals(MODE_TEST_PRED)) {
                    checkArgsFiles(cmd);
                    handlePredictionTest(cmd.getOptionValue("pf"), cmd.getOptionValue("tf"));
                } else if(arg.equals(MODE_TEST_HIST)) {
                    checkArgsFiles(cmd);
                    handleHistoryTest(cmd.getOptionValue("pf"), cmd.getOptionValue("tf"));
                } else {
                    reportError("Invalid Mode Definition");
                }
            } else {
                help(options);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void handleHistoryTest(String programPath, String testPath) {
        try {
            List<GenProgram> programs = JSONPrinterCaller.getPrograms(programPath);
            TestFileMarker.markNodes(programs, testPath);


            // read in all the JSONESTrees for the variable names
            File f = new File(programPath);
            BufferedReader r = null;
            int MAX = programs.size();
            int numIt = 0;
            JSONESTree[] trees = new JSONESTree[programs.size()];
            try {
                r = new BufferedReader(new FileReader(f));
                String json = r.readLine();
                while (json !=null && !json.isEmpty() && numIt < MAX) {
                    trees[numIt] = JSONESTree.parseLine(json);
                    json = r.readLine();
                    numIt++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(r!=null)
                    try {
                        r.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            numIt = 0;

            for(GenProgram program: programs) {
                File temp = new File("./tmp.js");
                try {
                    FileUtil.writeToFile(program.getCode(), temp);
                    if(!program.getMarkedNodesIterator().hasNext()){
                        System.err.println("No marked node found");
                        System.exit(-1);
                    }
                    // go through all the marked nodes for one program
                    for(Iterator<Integer> it = program.getMarkedNodesIterator(); it.hasNext();){
                        int markedNodeID = it.next();
                        String variablename = trees[numIt].getNodes().get(markedNodeID).getValue();
                        SourceLocation loc = program.getSourceLocationForID(markedNodeID);
                        HistoryExtraction extr = new HistoryExtraction(temp.getPath(), loc.getLineNumber(), loc.getColumnNumber(), variablename, numIt, markedNodeID);
                        String extrHist = extr.getExtractedHistories();
                        // remove the last new line
                        System.out.print(extrHist);
                    }


                } finally {
                    if(temp.exists())
                        if(!temp.delete())
                            System.err.println("Failed deleting temp");
                    numIt++;
                }
            }
        } catch (IOException e) {
            reportError("Error occurred :(");
        }


    }

    public static void handlePredictionTest(String programPath, String testPath) {
        try {
            List<GenProgram> programs = JSONPrinterCaller.getPrograms(programPath);
            TestFileMarker.markNodes(programs, testPath);

            for(GenProgram program: programs) {
                File temp = new File("./tmp.js");
                try {
                    FileUtil.writeToFile(program.getCode(), temp);
                    if(!program.getMarkedNodesIterator().hasNext())
                        System.err.println("No marked node found");
                        System.exit(-1);
                    int markedNodeID = program.getMarkedNodesIterator().next();
                    SourceLocation loc = program.getSourceLocationForID(markedNodeID);
                    PredictionHistory pred = new PredictionHistory(temp.getPath(), loc.getLineNumber(), loc.getColumnNumber());
                    System.out.println(pred.getPredictionHistories());
                } finally {
                    if(temp.exists())
                        if(!temp.delete())
                           System.err.println("Failed deleting temp");
                }

            }
        } catch (Exception e) {
            reportError("Error occurred :(");
        }
    }

    public static void main(String[] args) {
        // see https://commons.apache.org/proper/commons-cli/usage.html for CLI api
        parseArgs(args);
    }
}
