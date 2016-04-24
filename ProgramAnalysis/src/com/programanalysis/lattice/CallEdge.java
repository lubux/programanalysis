package com.programanalysis.lattice;

import dk.brics.tajs.solver.ICallEdge;

/**
 * Created by cedri on 4/24/2016.
 */
public class CallEdge implements ICallEdge<State> {

    private State state;

    public CallEdge(State state){
        this.state = state;
    }
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}
