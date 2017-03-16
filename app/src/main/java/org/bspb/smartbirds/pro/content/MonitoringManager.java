package org.bspb.smartbirds.pro.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.db.FormColumns;
import org.bspb.smartbirds.pro.db.MonitoringColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;
import org.bspb.smartbirds.pro.db.TrackingColumns;
import org.bspb.smartbirds.pro.enums.EntryType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.ContentUris.parseId;
import static java.lang.Double.parseDouble;
import static org.androidannotations.annotations.EBean.Scope.Singleton;
import static org.bspb.smartbirds.pro.content.Monitoring.Status.wip;

/**
 * Created by groupsky on 05.12.16.
 */

@EBean(scope = Singleton)
public class MonitoringManager {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
    private static final Gson SERIALIZER = new Gson();
    public static final String[] MONITORING_PROJECTION = {
            MonitoringColumns._ID,
            MonitoringColumns.STATUS,
            MonitoringColumns.DATA
    };
    private static final String[] ENTRY_PROJECTION = {
            FormColumns._ID,
            FormColumns.DATA
    };

    static {
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @StringRes(R.string.tag_lat)
    static String tagLatitude;
    @StringRes(R.string.tag_lon)
    static String tagLongitude;

    ContentResolver contentResolver;

    public MonitoringManager(Context context) {
        this.contentResolver = context.getApplicationContext().getContentResolver();
    }

    @NonNull
    private String generateMonitoringCode() {
        String uuid = UUID.randomUUID().toString();
        return String.format("%s-%s", DATE_FORMATTER.format(new Date()), uuid.substring(uuid.length() - 12));
    }

    @NonNull
    public Monitoring createNew() {
        String code = generateMonitoringCode();
        Monitoring monitoring = new Monitoring(code);
        monitoring.id = parseId(contentResolver.insert(SmartBirdsProvider.Monitorings.CONTENT_URI, toContentValues(monitoring)));
        return monitoring;
    }

    public void update(@NonNull Monitoring monitoring) {
        contentResolver.update(SmartBirdsProvider.Monitorings.withCode(monitoring.code), toContentValues(monitoring), null, null);
    }

    public void updateStatus(@NonNull String monitoringCode, @NonNull Monitoring.Status status) {
        ContentValues cv = new ContentValues();
        cv.put(MonitoringColumns.STATUS, status.name());
        contentResolver.update(SmartBirdsProvider.Monitorings.withCode(monitoringCode), cv, null, null);
    }

    public void updateStatus(@NonNull Monitoring monitoring, @NonNull Monitoring.Status status) {
        updateStatus(monitoring.code, status);
        monitoring.status = status;
    }

    private static ContentValues toContentValues(@NonNull Monitoring monitoring) {
        ContentValues cv = new ContentValues();
        cv.put(MonitoringColumns.CODE, monitoring.code);
        cv.put(MonitoringColumns.STATUS, monitoring.status.name());
        cv.put(MonitoringColumns.DATA, SERIALIZER.toJson(monitoring));
        return cv;
    }

    private static ContentValues toContentValues(@NonNull MonitoringEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(FormColumns.CODE, entry.monitoringCode);
        cv.put(FormColumns.TYPE, entry.type.name());
        cv.put(FormColumns.LATITUDE, parseDouble(entry.data.get(tagLatitude)));
        cv.put(FormColumns.LONGITUDE, parseDouble(entry.data.get(tagLongitude)));
        cv.put(FormColumns.DATA, SERIALIZER.toJson(entry));
        return cv;
    }

    private static ContentValues toContentValues(@NonNull TrackingLocation location) {
        ContentValues cv = new ContentValues();
        cv.put(TrackingColumns.CODE, location.monitoringCode);
        cv.put(TrackingColumns.TIME, location.time);
        cv.put(TrackingColumns.LATITUDE, location.latitude);
        cv.put(TrackingColumns.LONGITUDE, location.longitude);
        cv.put(TrackingColumns.ALTITUDE, location.altitude);
        return cv;
    }

    public static Monitoring monitoringFromCursor(@NonNull Cursor cursor) {
        Monitoring monitoring = SERIALIZER.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(MonitoringColumns.DATA)), Monitoring.class);
        final int idIdx = cursor.getColumnIndex(MonitoringColumns._ID);
        if (idIdx != -1) {
            monitoring.id = cursor.getLong(idIdx);
        }
        final int statusIdx = cursor.getColumnIndex(MonitoringColumns.STATUS);
        if (statusIdx != -1) {
            monitoring.status = Monitoring.Status.valueOf(cursor.getString(statusIdx));
        }
        return monitoring;
    }

    public static MonitoringEntry entryFromCursor(@NonNull Cursor cursor) {
        MonitoringEntry entry = SERIALIZER.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(FormColumns.DATA)), MonitoringEntry.class);
        final int idx = cursor.getColumnIndex(FormColumns._ID);
        if (idx != -1) {
            entry.id = cursor.getLong(idx);
        }
        return entry;
    }

    public static TrackingLocation locationFromCursor(@NonNull Cursor cursor) {
        String monitoringCode = cursor.getString(cursor.getColumnIndexOrThrow(TrackingColumns.CODE));
        long time = cursor.getLong(cursor.getColumnIndexOrThrow(TrackingColumns.TIME));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(TrackingColumns.LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(TrackingColumns.LONGITUDE));
        Double altitude = null;
        final int altitudeIdx = cursor.getColumnIndex(TrackingColumns.ALTITUDE);
        if (altitudeIdx != -1 && !cursor.isNull(altitudeIdx))
            altitude = cursor.getDouble(altitudeIdx);

        TrackingLocation location = new TrackingLocation(monitoringCode, time, latitude, longitude, altitude);

        final int idIdx = cursor.getColumnIndex(TrackingColumns._ID);
        if (idIdx != -1)
            location.id = cursor.getLong(idIdx);

        return location;
    }

    public MonitoringEntry newEntry(@NonNull Monitoring monitoring, @NonNull EntryType entryType, @NonNull HashMap<String, String> data) {
        MonitoringEntry entry = new MonitoringEntry(monitoring.code, entryType);
        entry.data.putAll(data);
        entry.id = parseId(contentResolver.insert(SmartBirdsProvider.Forms.CONTENT_URI, toContentValues(entry)));
        return entry;
    }

    public MonitoringEntry updateEntry(@NonNull String monitoringCode, long entryId, @NonNull EntryType entryType, @NonNull HashMap<String, String> data) {
        MonitoringEntry entry = new MonitoringEntry(monitoringCode, entryType);
        entry.data.putAll(data);
        entry.id = entryId;
        contentResolver.update(SmartBirdsProvider.Forms.withId(entryId), toContentValues(entry), null, null);
        return entry;
    }

    public TrackingLocation newTracking(Monitoring monitoring, Location location) {
        TrackingLocation l = new TrackingLocation(monitoring.code, location);
        l.id = parseId(contentResolver.insert(SmartBirdsProvider.Tracking.CONTENT_URI, toContentValues(l)));
        return l;
    }

    public boolean deleteLastEntry(@NonNull Monitoring monitoring) {
        long rowId;
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Forms.withMonitoringCode(monitoring.code), new String[] {FormColumns._ID}, null, null, FormColumns._ID+" desc");
        if (cursor == null) return false;
        try {
            if (!cursor.moveToFirst()) return false;
            if (cursor.isAfterLast()) return false;
            rowId = cursor.getLong(cursor.getColumnIndexOrThrow(FormColumns._ID));
        } finally {
            cursor.close();
        }
        return contentResolver.delete(SmartBirdsProvider.Forms.withId(rowId), null, null) == 1;
    }

    public Monitoring getMonitoring(@NonNull String monitoringCode) {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.withCode(monitoringCode), MONITORING_PROJECTION, null, null, null);
        if (cursor != null) try {
            return monitoringFromCursor(cursor);
        } finally {
            cursor.close();
        }
        return null;
    }

    public Cursor getEntries(Monitoring monitoring, EntryType entryType) {
        return contentResolver.query(SmartBirdsProvider.Forms.withMonitoringCode(monitoring.code), ENTRY_PROJECTION, FormColumns.TYPE+"=?", new String[]{entryType.name()}, FormColumns._ID);
    }

    public Monitoring getActiveMonitoring() {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.CONTENT_URI, MONITORING_PROJECTION, MonitoringColumns.STATUS + "=?", new String[]{wip.name()}, MonitoringColumns._ID + " desc");
        if (cursor == null) return null;
        try {
            if (!cursor.moveToFirst()) return null;
            return monitoringFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public Iterable<String> monitoringCodesForStatus(Monitoring.Status status) {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.CONTENT_URI,
                new String[] {MonitoringColumns.CODE},
                MonitoringColumns.STATUS+"=?", new String[]{status.name()},
                MonitoringColumns._ID);
        if (cursor == null) return new ArrayList<>(0);
        try {
            List<String> monitoringCodes = new ArrayList<>(cursor.getCount());
            int codeIdx = cursor.getColumnIndexOrThrow(MonitoringColumns.CODE);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
                monitoringCodes.add(cursor.getString(codeIdx));
            return monitoringCodes;
        } finally {
            cursor.close();
        }
    }

    public int countMonitoringsForStatus(Monitoring.Status status) {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.CONTENT_URI,
                new String[] {MonitoringColumns.CODE},
                MonitoringColumns.STATUS+"=?", new String[]{status.name()},
                MonitoringColumns._ID);
        if (cursor == null) return 0;
        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }
}
