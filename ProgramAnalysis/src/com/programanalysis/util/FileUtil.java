package com.programanalysis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lukas on 05.05.16.
 */
public class FileUtil {

    public static String getWorkingDirectory() {
        try {
            return new File( "." ).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return "./";
        }
    }

    public static String makePath(String... path) {
        if(path == null || path.length < 1)
            throw new IllegalArgumentException("Illegal path argument");
        StringBuilder sb = new StringBuilder();
        for(int idx=0; idx<path.length-1; idx++)
            sb.append(path[idx]).append(File.separator);
        sb.append(path[path.length-1]);
        return sb.toString();
    }

    public static void writeToFile(String text, File out) throws IOException {
        BufferedWriter fw = null;
        try {
            fw = new BufferedWriter(new FileWriter(out));
            fw.write(text);
        } finally {
            if(fw!=null)
                fw.close();
        }
    }

    public static String getNodeJSCallGraphPath() {
        return makePath(getWorkingDirectory(), "javascript-call-graph-master", "main.js");
    }

    public static String getNodeJSCommand() {
        if(OSTestHelper.getOperatingSystemType().equals(OSTestHelper.OSType.Linux))
            return "nodejs";
        return "node";
    }

    public static String getCallGraphFlag() {
        return "--cg";
    }

    public static String getNodeJSCallGraphCMD(String filePath) {
        return getNodeJSCommand()
                + " " + getNodeJSCallGraphPath()
                + " " + getCallGraphFlag()
                + " " + filePath;
    }

}
