package org.bspb.smartbirds.pro.adapter;

import android.database.Cursor;

/**
 * Created by groupsky on 08.03.17.
 */
public interface ModelCursorFactory<T> {

    T createModelFromCursor(Cursor cursor);

}
