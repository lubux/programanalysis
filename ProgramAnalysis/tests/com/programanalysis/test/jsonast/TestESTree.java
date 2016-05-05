package com.programanalysis.test.jsonast;

import com.programanalysis.jsonast.ESTree;
import com.programanalysis.jsonast.JSONESTree;
import com.programanalysis.util.FileUtil;
import org.junit.Test;

import java.io.*;

/**
 * Created by lukas on 05.05.16.
 */
public class TestESTree {

    @Test
    public void testParsing() {
        File f = new File(FileUtil.makePath("data", "jsonast", "simpleprogram.json"));
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(f));
            String json = r.readLine();
            while (json !=null && !json.isEmpty()) {
                JSONESTree tree = JSONESTree.parseLine(json);
                ESTree estree = ESTree.buildTree(tree);
                //System.out.println(estree.toString());
                json = r.readLine();
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
    }

    @Test
    public void testParsingLarge() {
        int MAX = 10000;
        int numIt = 0;
        File f = new File(FileUtil.makePath("..","js_dataset", "programs_training.json"));
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(f));
            String json = r.readLine();
            while (json !=null && !json.isEmpty() && numIt < MAX) {
                JSONESTree tree = JSONESTree.parseLine(json);
                ESTree estree = ESTree.buildTree(tree);
                //System.out.println(estree.toString());
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

    }
}
