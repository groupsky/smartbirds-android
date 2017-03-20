package org.bspb.smartbirds.pro.adapter;

import android.widget.Adapter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by groupsky on 20.03.17.
 */
public class AdapterIterator<T> implements Iterator<T> {
    private final Adapter adapter;
    private int position = -1;

    public AdapterIterator(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean hasNext() {
        return adapter.getCount() > ++position;
    }

    @Override
    public T next() {
        if (adapter.getCount() <= position) throw new NoSuchElementException();
        //noinspection unchecked
        return (T) adapter.getItem(position);
    }
}
