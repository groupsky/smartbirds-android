package org.bspb.smartbirds.pro.collections;

import java.util.Iterator;

/**
 * Created by groupsky on 20.03.17.
 */

public class IteratorConverter<T, Y> implements Iterator<T> {

    private final Iterator<Y> iterator;
    private final Converter<Y, T> converter;

    public IteratorConverter(Iterator<Y> iterator, Converter<Y, T> converter) {
        this.iterator = iterator;
        this.converter = converter;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return converter.convert(iterator.next());
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
