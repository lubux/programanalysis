package com.programanalysis.util;

import java.io.File;
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

    public static String getNodeJSCallGraphPath() {
        return getWorkingDirectory() + File.separator + "javascript-call-graph-master" + File.separator +"main.js";
    }

    public static String getNodeJSCommand() {
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
