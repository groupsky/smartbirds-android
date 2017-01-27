package org.bspb.smartbirds.pro.tools;

import java.lang.reflect.Array;

/**
 * Created by groupsky on 27.01.17.
 */

public class ArrayUtils {

    public static <T> T[] join(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

}
