package org.bspb.smartbirds.pro.beans;

import android.database.Cursor;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.bspb.smartbirds.pro.adapter.CursorIterable;
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory;
import org.bspb.smartbirds.pro.backend.dto.Zone;
import org.bspb.smartbirds.pro.content.MonitoringEntry;
import org.bspb.smartbirds.pro.content.MonitoringManager;

import java.util.ArrayList;

/**
 * Created by groupsky on 23.03.17.
 */

@EBean
public class ZonesModelEntries implements ZoneCursorEntries.Listener {

    @Bean
    ZoneCursorEntries cursorEntries;

    private Listener listener;

    @AfterInject
    protected void setup() {
        cursorEntries.setListener(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onZoneCursorChanged(ZoneCursorEntries entries) {
        if (listener != null) listener.onZoneEntriesChanged(this);
    }

    public Iterable<Zone> iterable() {
        Cursor cursor = cursorEntries.getCursor();
        if (cursor == null) return new ArrayList<>(0);
        return new CursorIterable<>(cursor, new ZoneFactory());
    }

    private static class MonitoringEntryFactory implements ModelCursorFactory<MonitoringEntry> {
        @Override
        public MonitoringEntry createModelFromCursor(Cursor cursor) {
            return MonitoringManager.entryFromCursor(cursor);
        }
    }

    public interface Listener {
        void onZoneEntriesChanged(ZonesModelEntries entries);
    }

    static class ZoneFactory implements ModelCursorFactory<Zone> {

        @Override
        public Zone createModelFromCursor(Cursor cursor) {
            return Zone.fromCursor(cursor);
        }
    }

}
