package com.programanalysis.lattice;

import dk.brics.tajs.solver.IContext;

/**
 * Created by cedri on 4/24/2016.
 */
public class Context implements IContext<Context> {

    public Context(){

    }

    @Override
    public Context makeEntryContext() {
        return new Context();
    }
}
