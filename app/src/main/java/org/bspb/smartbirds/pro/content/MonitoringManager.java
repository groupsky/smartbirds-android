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

    Context rootContext;

    public MonitoringManager(Context context) {
        this.rootContext = context.getApplicationContext();
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
