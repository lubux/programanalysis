package com.programanalysis.test;

import java.util.Locale;

/**
 * Created by lukas on 10.05.16.
 */
public class OSTestHelper {

    public enum OSType {
        Windows, MacOS, Linux, Other
    }

    private static OSType detectedOS = null;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || OS.contains("darwin")) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }
}
