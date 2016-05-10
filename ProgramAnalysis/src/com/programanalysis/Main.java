package com.programanalysis;

import com.programanalysis.jsonast.GenProgram;
import com.programanalysis.jsonast.JSONPrinterCaller;
import com.programanalysis.jsonast.TestFileMarker;
import org.apache.commons.cli.*;
import java.io.File;
import java.io.IOException;
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

            //TODO: Start analysis
        } catch (IOException e) {
            reportError("Error occurred :(");
        }


    }

    public static void handlePredictionTest(String programPath, String testPath) {
        try {
            List<GenProgram> programs = JSONPrinterCaller.getPrograms(programPath);
            TestFileMarker.markNodes(programs, testPath);

            //TODO: Start analysis + predidction
        } catch (IOException e) {
            reportError("Error occurred :(");
        }
    }

    public static void main(String[] args) {
        // see https://commons.apache.org/proper/commons-cli/usage.html for CLI api
        parseArgs(args);
    }
}
