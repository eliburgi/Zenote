package com.eliburgi.zenote.utilities;

/**
 * Created by Elias on 21.01.2017.
 */

public class Preconditions {

    public static Object checkNotNull(Object o) {
        if(o == null) {
            throw new NullPointerException("Object " + o + " is null!");
        }
        return o;
    }

    public static Object checkNotNull(Object o, String message) {
        if(o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }
}
