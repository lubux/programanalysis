package com.programanalysis.test.util;

import com.programanalysis.jsonast.GenProgram;
import com.programanalysis.jsonast.JSONPrinterCaller;
import com.programanalysis.util.FileUtil;
import com.programanalysis.jsonast.JSONPrinterParser;
import dk.brics.tajs.flowgraph.SourceLocation;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by lukas on 09.05.16.
 */
public class TestJSONPrinterParser {

    @Test
    public void testParsing() {
        File f = new File(FileUtil.makePath("data", "jsonast", "out_data.txt"));
        InputStream data = null;
        try {
            data = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        List<String> results = JSONPrinterParser.parseJSONPrinterOutput(data);
        int i = 1;
        for (String res: results) {
            System.out.println("-------Result "+i+"-------");
            System.out.println(res);
            System.out.println("----------------------");
            i++;
        }
        Assert.assertEquals(results.size(), 5);
    }

    @Test
    public void testParsingID() {
        File f = new File(FileUtil.makePath("data", "jsonast", "out_ids.txt"));
        InputStream data = null;
        try {
            data = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        List<Map<SourceLocation, Integer>> results = JSONPrinterParser.parseJSONPrinterIDOutput(data);
        int i = 1;
        for (Map<SourceLocation, Integer> res: results) {
            System.out.println("-------Result "+i+"-------");
            System.out.println(res);
            System.out.println("----------------------");
            i++;
        }
        Assert.assertEquals(results.size(), 5);
    }

    @Test
    public void testCombined() {
        File f1 = new File(FileUtil.makePath("data", "jsonast", "out_data.txt"));
        File f2 = new File(FileUtil.makePath("data", "jsonast", "out_ids.txt"));
        InputStream data1 = null;
        InputStream data2 = null;
        try {
            data1 = new FileInputStream(f1);
            data2 = new FileInputStream(f2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        List<GenProgram> results = JSONPrinterParser.parseGenPrograms(data1, data2);
        for (GenProgram res: results) {
            System.out.println(res);
        }
        Assert.assertEquals(results.size(), 5);
    }

    @Test
    public void testCaller() {
        String path = FileUtil.makePath("data", "jsonast", "simpleprogram.json");
        List<GenProgram> programs = JSONPrinterCaller.getPrograms(path, 5);
        for (GenProgram res: programs) {
            System.out.println(res);
        }
        Assert.assertEquals(programs.size(), 5);
    }
}

