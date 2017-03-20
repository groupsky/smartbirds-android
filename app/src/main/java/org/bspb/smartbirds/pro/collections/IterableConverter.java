package org.bspb.smartbirds.pro.collections;

import java.util.Iterator;

/**
 * Created by groupsky on 20.03.17.
 */

public class IterableConverter<T, Y> implements Iterable<T> {

    private final Iterable<Y> source;
    private final Converter<Y, T> converter;

    public IterableConverter(Iterable<Y> source, Converter<Y, T> converter) {
        this.source = source;
        this.converter = converter;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorConverter<>(source.iterator(), converter);
    }
}
