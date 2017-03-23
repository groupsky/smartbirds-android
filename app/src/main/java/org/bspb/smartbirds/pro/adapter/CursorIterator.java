package org.bspb.smartbirds.pro.adapter;

import android.database.Cursor;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by groupsky on 23.03.17.
 */

class CursorIterator<T> implements Iterator<T> {
    private final Cursor cursor;
    private final ModelCursorFactory<T> factory;
    private T model;

    public CursorIterator(Cursor cursor, ModelCursorFactory<T> factory) {
        this.cursor = cursor;
        this.factory = factory;
        cursor.moveToFirst();
    }

    @Override
    public boolean hasNext() {
        model = null;
        if (cursor.isAfterLast()) return false;

        model = factory.createModelFromCursor(cursor);
        cursor.moveToNext();
        return true;
    }

    @Override
    public T next() {
        if (model == null) throw new NoSuchElementException();
        return model;
    }
}
