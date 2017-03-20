package org.bspb.smartbirds.pro.adapter;

import android.widget.Adapter;

import java.util.Iterator;

/**
 * Created by groupsky on 20.03.17.
 */

public class AdapterIterable<T> implements Iterable<T> {

    private Adapter adapter;

    public AdapterIterable(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Iterator<T> iterator() {
        return new AdapterIterator<>(adapter);
    }

}
