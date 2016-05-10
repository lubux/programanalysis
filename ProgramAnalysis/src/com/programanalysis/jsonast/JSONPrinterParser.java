package com.programanalysis.jsonast;

import dk.brics.tajs.flowgraph.SourceLocation;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lukas on 09.05.16.
 */
public class JSONPrinterParser {

    public static final String PATTERN_LOG_STR = "I\\d+ .+ \\d* main.cpp:\\d*] (.*)";
    public static final String PATTERN_LOG_ID = "I\\d+ .+ \\d* main.cpp:\\d*] (\\d+) (\\d+) (\\d+)";

    public static final Pattern PATTERN_LOG = Pattern.compile(PATTERN_LOG_STR);
    public static final Pattern PATTERN_ID = Pattern.compile(PATTERN_LOG_ID);

    public static final String FILE_NAME_IDENTIFIER = "ProgramInLine";

    /**
     * Parses the Output of the AST printer
     * (http://www.srl.inf.ethz.ch/pa2016/json_printer_v1.tar.gz)
     * (./bin/syntree/main --num_data_records=10 --data=simpleprogram.json --logtostderr 2> out_data.txt)
     * @param input the inpustream of the output
     * @return A list of java script programs
     */
    public static List<String> parseJSONPrinterOutput(InputStream input) {
        BufferedReader reader = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            int skip = 0;
            boolean inRead = false;
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            while (line!=null && !line.isEmpty()) {
                Matcher matcher =  PATTERN_LOG.matcher(line);
                if (matcher.find()) {
                    if(skip<2) {
                        skip++;
                        line = reader.readLine();
                        continue;
                    }
                    if(sb.length()>0) {
                        result.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    String start = matcher.group(1);
                    if(start!=null && !start.isEmpty()) {
                        sb.append(start);
                        sb.append(System.lineSeparator());
                    }
                    inRead = true;
                } else if(inRead) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }

                line = reader.readLine();
            }
            if (sb.length() > 0) {
                result.add(sb.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        } finally {
            if(reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    /**
     * Parses the Output of the AST printer with flag info
     * (http://www.srl.inf.ethz.ch/pa2016/json_printer_v1.tar.gz)
     * (./bin/syntree/main --num_data_records=10 --data=simpleprogram.json --mode=info --logtostderr 2> out.txt)
     * @param input the inputstream of the output
     * @return a list of maps for each program, the map contains mappings from SourceLocation to the id of the node
     */
    public static List<List<LocIdPair>> parseJSONPrinterIDOutput(InputStream input) {
        BufferedReader reader = null;
        List<List<LocIdPair>> result = new ArrayList<>();
        int curfile = 1;
        try {
            int curID = 0;
            reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            List<LocIdPair> curMap = new ArrayList<>();
            while (line!=null && !line.isEmpty()) {
                Matcher matcher =  PATTERN_ID.matcher(line);
                if (matcher.find()) {
                    int lineNr = Integer.valueOf(matcher.group(2));
                    if(curID>lineNr) {
                        result.add(curMap);
                        curMap = new ArrayList<>();
                    }
                    SourceLocation loc = new SourceLocation(lineNr, Integer.valueOf(matcher.group(3)), FILE_NAME_IDENTIFIER+curfile);
                    curMap.add(new LocIdPair(loc, Integer.valueOf(matcher.group(1))));
                    curID = lineNr;
                }
                line = reader.readLine();
            }
            if (!curMap.isEmpty()) {
                result.add(curMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        } finally {
            if(reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static List<GenProgram> parseGenPrograms(InputStream dataOutput, InputStream idOutput) {
        List<String> outs = parseJSONPrinterOutput(dataOutput);
        List<List<LocIdPair>> maps = parseJSONPrinterIDOutput(idOutput);
        Iterator<String> itOuts = outs.listIterator();
        Iterator<List<LocIdPair>> itMaps = maps.iterator();
        ArrayList<GenProgram> programs = new ArrayList<>();
        int i = 0;
        while (itOuts.hasNext() && itMaps.hasNext()) {
            programs.add(new GenProgram(i, FILE_NAME_IDENTIFIER+i, itOuts.next(), itMaps.next()));
            i++;
        }
        return programs;
    }

}
