package com.programanalysis.util;

import dk.brics.tajs.flowgraph.BasicBlock;

/**
 * Created by cedri on 4/28/2016.
 * objects that are put in the worklist
 */
public class QueueEntry implements Comparable<QueueEntry> {
    private static int serialNumber;
    public QueueEntry(BasicBlock block) {
        this.block=block;
        this.serial = serialNumber++;
    }

    private int serial;

    private BasicBlock block;

    public BasicBlock getBlock(){
        return block;
    }
    public int getSerial(){
        return serial;
    }
    @Override
    /** from workliststrategy of tajs*/
    public int compareTo(QueueEntry o) {
        BasicBlock block2 = o.getBlock();
        int serial2 = o.getSerial();

        if(serial == serial2){
            return 0;
        }
        if(block.getFunction().equals(block2.getFunction())){
            if(block.getOrder() < block2.getOrder()){
                return -1;
            } else if(block.getOrder() > block2.getOrder()){
                return 1;
            }
        }
        return serial - serial2;
    }
}
