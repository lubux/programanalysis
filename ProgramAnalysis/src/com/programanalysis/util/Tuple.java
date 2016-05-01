package com.programanalysis.util;

import com.programanalysis.lattice.AbstractObject;

/**
 * Created by cedri on 4/24/2016.
 *
 * A tuple class to the field store
 */
public class Tuple {
    public AbstractObject a;

    public String b;

    public Tuple(AbstractObject a, String b){
        this.a = a;
        this.b = b;
    }

    public boolean equals(Object o){
        if(! (o instanceof Tuple)){
            return false;
        }
        Tuple tup = (Tuple) o;
        if(a.equals(tup.a)){
            if(b.equals(tup.b)){
                return true;
            }
        }
        return false;
    }

    public int hashCode(){
        return 15*a.hashCode() + b.hashCode();
    }
}
