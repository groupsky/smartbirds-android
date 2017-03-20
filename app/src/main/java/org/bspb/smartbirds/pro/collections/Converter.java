package org.bspb.smartbirds.pro.collections;

/**
 * Created by groupsky on 20.03.17.
 */

public interface Converter<A, B> {

    B convert(A item);

}
