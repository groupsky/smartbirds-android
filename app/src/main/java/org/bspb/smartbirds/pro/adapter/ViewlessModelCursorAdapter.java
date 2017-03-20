package org.bspb.smartbirds.pro.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by groupsky on 20.03.17.
 */

public class ViewlessModelCursorAdapter<T> extends ModelCursorAdapter<T> {

    public ViewlessModelCursorAdapter(Context context, Cursor c, ModelCursorFactory<T> factory) {
        super(context, View.NO_ID, c, factory);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new View(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // intentionally left empty
    }

    @Override
    public void bindView(View view, Context context, T model) {
        // intentionally left empty
    }
}
