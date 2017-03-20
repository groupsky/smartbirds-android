package org.bspb.smartbirds.pro.beans;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.bspb.smartbirds.pro.adapter.AdapterIterable;
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory;
import org.bspb.smartbirds.pro.adapter.ViewlessModelCursorAdapter;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;

/**
 * Created by groupsky on 20.03.17.
 */

@EBean
public class MonitoringModelEntries implements MonitoringCursorEntries.Listener {

    @RootContext
    Context context;
    @Bean
    MonitoringCursorEntries cursorEntries;

    private Listener listener;

    /**
     * Adapter to reuse cursor update changes
     */
    private ViewlessModelCursorAdapter<MonitoringEntry> adapter;

    /**
     * Observer for changes from adapter
     */
    private DataSetObserver datasetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataChanged();
        }
    };
    private String monitoringCode;

    @AfterInject
    protected void setup() {
        cursorEntries.setListener(this);
        cursorEntries.setMonitoringCode(monitoringCode);
        adapter = new ViewlessModelCursorAdapter<>(context, cursorEntries.getCursor(), new MonitoringEntryFactory());
        adapter.registerDataSetObserver(datasetObserver);
    }

    void onDataChanged() {
        if (listener != null) {
            listener.onMonitoringEntriesChanged(this);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setMonitoringCode(String monitoringCode) {
        this.monitoringCode = monitoringCode;
        if (cursorEntries != null) cursorEntries.setMonitoringCode(monitoringCode);
    }

    @Override
    public void onMonitoringEntriesChanged(MonitoringCursorEntries entries) {
        if (adapter != null) adapter.swapCursor(entries.getCursor());
    }

    public Iterable<MonitoringEntry> iterable() {
        return new AdapterIterable<>(adapter);
    }

    private static class MonitoringEntryFactory implements ModelCursorFactory<MonitoringEntry> {
        @Override
        public MonitoringEntry createModelFromCursor(Cursor cursor) {
            return MonitoringManager.entryFromCursor(cursor);
        }
    }

    public interface Listener {
        void onMonitoringEntriesChanged(MonitoringModelEntries entries);
    }

}
