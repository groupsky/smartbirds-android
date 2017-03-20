package org.bspb.smartbirds.pro.beans;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.db.FormColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;

import java.util.Locale;

import static android.text.TextUtils.isEmpty;

/**
 * Created by groupsky on 20.03.17.
 */

@EBean
public class MonitoringCursorEntries implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SmartBirdsApplication.TAG + ".MonCurEn";
    private static final String[] PROJECTION = {
            FormColumns._ID,
            FormColumns.DATA,
            FormColumns.TYPE,
            FormColumns.CODE,
            FormColumns.LATITUDE,
            FormColumns.LONGITUDE,
    };

    @RootContext
    protected Activity activity;
    private String monitoringCode;
    private Cursor cursor;
    private CharSequence lastMonitoringCode;
    private Listener listener;

    @AfterInject
    protected void setup() {
        restartLoader();
    }

    private void restartLoader() {
        if (activity == null) return;
        if (isEmpty(monitoringCode)) return;
        if (TextUtils.equals(monitoringCode, lastMonitoringCode)) return;
        activity.getLoaderManager().restartLoader(hashCode(), null, this);
    }

    public void setMonitoringCode(String monitoringCode) {
        this.monitoringCode = monitoringCode;
        restartLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        lastMonitoringCode = monitoringCode;
        return new CursorLoader(activity,
                isEmpty(monitoringCode) ? SmartBirdsProvider.Forms.CONTENT_URI : SmartBirdsProvider.Forms.withMonitoringCode(this.monitoringCode),
                PROJECTION, null, null, FormColumns._ID + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, String.format(Locale.ENGLISH, "loaded %d", cursor.getCount()));
        this.cursor = cursor;
        if (listener != null) listener.onMonitoringEntriesChanged(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.cursor = null;
        if (listener != null) listener.onMonitoringEntriesChanged(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public interface Listener {
        void onMonitoringEntriesChanged(MonitoringCursorEntries entries);
    }
}
