package org.bspb.smartbirds.pro.beans;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;

import java.util.Locale;

/**
 * Created by groupsky on 23.03.17.
 */

@EBean
public class ZoneCursorEntries implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SmartBirdsApplication.TAG + ".ZonCurEn";

    @RootContext
    protected Activity activity;
    private Cursor cursor;
    private Listener listener;

    @AfterInject
    protected void setup() {
        initLoader();
    }

    private void initLoader() {
        if (activity == null) return;
        activity.getLoaderManager().initLoader(hashCode(), null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(activity, SmartBirdsProvider.Zones.CONTENT_URI, Zone.DEFAULT_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, String.format(Locale.ENGLISH, "loaded %d", cursor.getCount()));
        this.cursor = cursor;
        if (listener != null) listener.onZoneCursorChanged(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.cursor = null;
        if (listener != null) listener.onZoneCursorChanged(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public interface Listener {
        void onZoneCursorChanged(ZoneCursorEntries entries);
    }

}
