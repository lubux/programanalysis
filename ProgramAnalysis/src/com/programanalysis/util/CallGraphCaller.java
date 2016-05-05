package com.programanalysis.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static java.lang.Runtime.getRuntime;

/**
 * Created by cedri on 5/5/2016.
 */
public class CallGraphCaller {
    public static String getCallGraph(String path){
        Runtime r = getRuntime();
        Process p = null;
        try {
            p = r.exec(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = p.getInputStream();
        StringWriter writer = new StringWriter();
        String myString = null;
        try {
            myString = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myString;
    }
}
