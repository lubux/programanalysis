package com.programanalysis.PointerAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedri on 4/28/2016.
 * represents the set of registers for a basic block that are used by the node visitor
 */
public class BlockRegisters {
    public BlockRegisters(){
        registers = new ArrayList<Object>();
    }
    private List<Object> registers;

    public void writeRegister(int reg,Object obj){
        while (reg >= registers.size())
            registers.add(null);
        registers.set(reg, obj);
    }

    public Object readRegister(int reg){
        return registers.get(reg);
    }
}
