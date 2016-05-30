package com.programanalysis.util;

import java.util.TimerTask;

/**
 * Created by cedri on 5/16/2016.
 */
public class InterruptTimerTask extends TimerTask {

    private Thread theTread;

    public InterruptTimerTask(Thread theTread) {
        this.theTread = theTread;
    }

    @Override
    public void run() {
        theTread.interrupt();
    }

}
