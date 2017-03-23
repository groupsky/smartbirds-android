package org.bspb.smartbirds.pro.adapter;

import android.database.Cursor;

import java.util.Iterator;

/**
 * Created by groupsky on 23.03.17.
 */

public class CursorIterable<T> implements Iterable<T> {

    private final Cursor cursor;
    private final ModelCursorFactory<T> factory;

    public CursorIterable(Cursor cursor, ModelCursorFactory<T> factory) {
        this.cursor = cursor;
        this.factory = factory;
    }

    @Override
    public Iterator<T> iterator() {
        return new CursorIterator<T>(cursor, factory);
    }
}
