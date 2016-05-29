package com.programanalysis.test;

import com.programanalysis.Main;
import com.programanalysis.util.FileUtil;
import org.junit.Test;

/**
 * Created by lukas on 19.05.16.
 */
public class TestPredictionHist {

    @Test
    public void testPredHist() {
        String propath = FileUtil.makePath("data", "exampletest", "tests_suggest", "programs.json");
        String testpath = FileUtil.makePath("data", "exampletest", "tests_suggest", "test");
        Main.handlePredictionTest(propath, testpath);
    }

    @Test
    public void testHistTest() {
        String propath = FileUtil.makePath("data", "exampletest", "tests_histories", "programs.json");
        String testpath = FileUtil.makePath("data", "exampletest", "tests_histories", "test");
        Main.handleHistoryTest(propath, testpath);
    }

}
