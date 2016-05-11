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
        if(reg >= 0) {
            while (reg >= registers.size())
                registers.add(null);
            registers.set(reg, obj);
        }
    }

    public Object readRegister(int reg){
        if(reg < registers.size() && reg >= 0)
            return registers.get(reg);
        else
            return null;
    }

    public void deleteOrdinaryRegisters(){
        for(int i = 2; i < registers.size(); i++){
            registers.set(i, null);
        }
    }

    public void addOrdRegs(BlockRegisters other){
        for(int i = 2; i < other.registers.size(); i++){
            this.writeRegister(i, other.readRegister(i));
        }
    }
}
