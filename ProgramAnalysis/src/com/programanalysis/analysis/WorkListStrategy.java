package com.programanalysis.analysis;

import com.programanalysis.lattice.Context;
import dk.brics.tajs.solver.IWorkListStrategy;

/**
 * Created by cedri on 4/24/2016.
 */
public class WorkListStrategy implements IWorkListStrategy<Context> {
    @Override
    public int compare(IEntry<Context> iEntry, IEntry<Context> iEntry1) {
        return 0;
    }
}
