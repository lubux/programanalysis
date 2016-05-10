package com.programanalysis.jsonast;

import com.programanalysis.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Runtime.getRuntime;

/**
 * Created by lukas on 10.05.16.
 * Class for Calling the external ast_printer
 * (http://www.srl.inf.ethz.ch/pa2016/json_printer_v1.tar.gz)
 */
public class JSONPrinterCaller {


    private static final String PATH_TO_COMMAND = FileUtil.makePath(FileUtil.getWorkingDirectory() ,"ast_printer", "main");
    private static final String DATA_FLAG = "--data";
    private static final String NUM_RECORDS_FLAG = "--num_data_records";
    private static final String STDERR_FLAG = "--logtostderr";
    private static final String MODE_INFO_FLAG = "--mode=info";
    private static final String DELIM = " ";
    private static final String ASSIGN = "=";

    /**
     * Generates the javascript programs for the given json ast's in the file
     * @param filePath the path of the AST file
     * @param numPrograms the number of lines to consider ind the file (i.e. number of ast's)
     * @return a list of programs
     */
    public static List<GenProgram> getPrograms(String filePath, int numPrograms) {
        StringBuilder dataCMD = new StringBuilder(PATH_TO_COMMAND);
        StringBuilder idCMD;
        dataCMD.append(DELIM).append(NUM_RECORDS_FLAG)
                .append(ASSIGN).append(numPrograms).append(DELIM)
                .append(DATA_FLAG).append(ASSIGN).append(filePath).append(DELIM)
                .append(STDERR_FLAG);
        idCMD = new StringBuilder(dataCMD);
        idCMD.append(DELIM).append(MODE_INFO_FLAG);
        Process dataProcess = null;
        Process idProcess = null;
        List<GenProgram> result = null;
        try {
            dataProcess = getRuntime().exec(dataCMD.toString());
            dataProcess.waitFor();
            idProcess = getRuntime().exec(idCMD.toString());
            result = JSONPrinterParser.parseGenPrograms(dataProcess.getErrorStream(), idProcess.getErrorStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if(dataProcess!=null)
                dataProcess.destroy();
            if(idProcess!=null)
                idProcess.destroy();
        }
        return result;
    }

    /**
     * Generates the javascript programs for the given json ast's in the file
     * Uses default number of lines (1000)
     * @param filePath the path of the AST file
     * @return a list of programs
     */
    public static List<GenProgram> getPrograms(String filePath) {
        return getPrograms(filePath, 1000);
    }


}
