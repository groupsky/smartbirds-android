package org.bspb.smartbirds.pro.adapter;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by groupsky on 08.03.17.
 */

public abstract class ModelCursorAdapter<T> extends CursorAdapter {

    private final ModelCursorFactory<T> factory;
    private final LayoutInflater inflator;
    @LayoutRes
    private int layoutId;

    public ModelCursorAdapter(Context context, @LayoutRes int layoutId, Cursor c, ModelCursorFactory<T> factory) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
        this.layoutId = layoutId;
        this.factory = factory;
        this.inflator = LayoutInflater.from(context);
    }

    @Override
    public Object getItem(int position) {
        return getItemTyped(position);
    }

    public T getItemTyped(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        if (cursor == null) return null;
        return factory.createModelFromCursor(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflator.inflate(layoutId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        T model = factory.createModelFromCursor(cursor);
        bindView(view, context, model);
    }

    public abstract void bindView(View view, Context context, T model);

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }
}
