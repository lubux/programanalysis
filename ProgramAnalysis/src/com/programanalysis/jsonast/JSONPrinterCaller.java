package com.programanalysis.jsonast;

import com.programanalysis.util.FileUtil;

import java.io.IOException;
import java.util.List;

import static java.lang.Runtime.getRuntime;

/**
 * Created by lukas on 10.05.16.
 * Class for Calling the external ast_printer
 * (http://www.srl.inf.ethz.ch/pa2016/json_printer_v1.tar.gz)
 *--num_data_records=10 --data=simpleprogram.json --logtostderr 2> out_data.txt
 */
public class JSONPrinterCaller {


    private static final String PATH_TO_COMMAND = FileUtil.makePath(".","ast_printer", "main");
    private static final String DATA_FLAG = "--data";
    private static final String NUM_RECORDS_FLAG = "--num_data_records";
    private static final String STDERR_FLAG = "--logtostderr";
    private static final String MODE_INFO_FLAG = "--mode=info";
    private static final String DELIM = " ";
    private static final String ASSIGN = "=";


    public static List<GenProgram> getPrograms(String filePath, int numPrograms) {
        StringBuilder dataCMD = new StringBuilder(PATH_TO_COMMAND);
        StringBuilder idCMD;
        dataCMD.append(DELIM).append(NUM_RECORDS_FLAG)
                .append(ASSIGN).append(numPrograms).append(DELIM)
                .append(DATA_FLAG).append(ASSIGN).append(filePath).append(DELIM)
                .append(STDERR_FLAG);
        idCMD = new StringBuilder(dataCMD);
        idCMD.append(DELIM).append(MODE_INFO_FLAG);
        Runtime runtime = getRuntime();
        Process dataProcess = null;
        Process idProcess = null;
        try {
            dataProcess = runtime.exec(dataCMD.toString());
            idProcess = runtime.exec(idCMD.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return JSONPrinterParser.parseGenPrograms(dataProcess.getErrorStream(), idProcess.getErrorStream());
    }
}
