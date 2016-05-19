package com.programanalysis.PointerAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        if(reg == 1){
            // we are dealing with the return register
            if(obj == null){
                return;
            }
            while (reg >= registers.size())
                registers.add(null);

            // we might have multiple return values and want to keep all of them
            if(registers.get(reg) != null){
                ((Set<AbstractObject>)registers.get(1)).addAll((Set<AbstractObject>)obj);
            } else{
                registers.set(reg, obj);
            }
        } else {
            // we are dealing with an ordinary register
            if(reg >= 0) {
                while (reg >= registers.size())
                    registers.add(null);
                registers.set(reg, obj);
            }
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

    public void addRegs(BlockRegisters other){
        for(int i = 0; i < other.registers.size(); i++){
            this.writeRegister(i, other.readRegister(i));
        }
    }
}
