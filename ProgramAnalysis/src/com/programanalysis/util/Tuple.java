package com.programanalysis.util;

/**
 * Created by cedri on 4/24/2016.
 *
 * A tuple class to the field store
 */
public class Tuple<G,E> {
    public G a;

    public E b;

    public Tuple(G a, E b){
        this.a = a;
        this.b = b;
    }

    public boolean equals(Tuple<G,E> tup){
        if(a.equals(tup.a)){
            if(b.equals(tup.b)){
                return true;
            }
        }
        return false;
    }
}
