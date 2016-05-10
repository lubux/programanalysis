package com.programanalysis.jsonast;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lukas on 10.05.16.
 */
public class TestFileMarker {

    private static final String TEST_PATTERN = "(\\d+)[ \\t]+(\\d+)";
    private static final Pattern testPattern = Pattern.compile(TEST_PATTERN);

    private static void markNode(List<GenProgram> programs, int lineId, int nodeId) {
        for (GenProgram program : programs) {
            if (program.getId()==lineId) {
                program.markNode(nodeId);
            }
        }
    }

    public static void markNodes(List<GenProgram> programs, String testFilePath) throws IOException {
        File testFile = new File(testFilePath);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(testFile));
            String line = reader.readLine();

            while (line!=null && !line.isEmpty()) {
                Matcher match = testPattern.matcher(line);
                if(match.find()) {
                    markNode(programs,
                            Integer.valueOf(match.group(1)),
                            Integer.valueOf(match.group(2)));
                }
                line = reader.readLine();
            }
        } finally {
            if(reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

}
