package org.bspb.smartbirds.pro.content;

import static android.content.ContentUris.parseId;
import static org.androidannotations.annotations.EBean.Scope.Singleton;
import static org.bspb.smartbirds.pro.content.Monitoring.Status.paused;
import static org.bspb.smartbirds.pro.content.Monitoring.Status.wip;
import static org.bspb.smartbirds.pro.tools.Reporting.logException;
import static java.lang.Double.parseDouble;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.location.Location;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.db.FormColumns;
import org.bspb.smartbirds.pro.db.MonitoringColumns;
import org.bspb.smartbirds.pro.db.SmartBirdsProvider;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.repository.TrackingRepository;
import org.bspb.smartbirds.pro.room.Tracking;
import org.bspb.smartbirds.pro.tools.SBGsonParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by groupsky on 05.12.16.
 */

@EBean(scope = Singleton)
public class MonitoringManager {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
    private static final Gson SERIALIZER = SBGsonParser.createParser();
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
    Context rootContext;

    public MonitoringManager(Context context) {
        this.rootContext = context.getApplicationContext();
        this.contentResolver = rootContext.getContentResolver();
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

        final int entriesIdx = cursor.getColumnIndex(MonitoringColumns.ENTRIES_COUNT);
        if (entriesIdx != -1) {
            monitoring.entriesCount = cursor.getInt(entriesIdx);
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

    public Monitoring getMonitoring(@NonNull String monitoringCode) {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.withCode(monitoringCode), MONITORING_PROJECTION, null, null, null);
        if (cursor != null) try {
            return cursor.moveToFirst() ? monitoringFromCursor(cursor) : null;
        } finally {
            cursor.close();
        }
        return null;
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
                new String[]{MonitoringColumns.CODE},
                MonitoringColumns.STATUS + "=?", new String[]{status.name()},
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
        int count = 0;
        try {
            Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.CONTENT_URI,
                    new String[]{MonitoringColumns.CODE},
                    MonitoringColumns.STATUS + "=?", new String[]{status.name()},
                    MonitoringColumns._ID);
            if (cursor != null)
                try {
                    count += cursor.getCount();
                } finally {
                    cursor.close();
                }
        } catch (Throwable t) {
            logException(t);
        }

        return count;
    }

    @Nullable
    public Monitoring getPausedMonitoring() {
        Cursor cursor = contentResolver.query(SmartBirdsProvider.Monitorings.CONTENT_URI, MONITORING_PROJECTION, MonitoringColumns.STATUS + "=?", new String[]{paused.name()}, MonitoringColumns._ID + " desc");
        if (cursor == null) return null;
        try {
            if (!cursor.moveToFirst()) return null;
            return monitoringFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    public void deleteMonitoring(String monitoringCode) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(SmartBirdsProvider.Forms.withMonitoringCode(monitoringCode)).build());
        ops.add(ContentProviderOperation.newDelete(SmartBirdsProvider.Monitorings.withCode(monitoringCode)).build());
        try {
            contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            logException(e);
        }
    }
}
