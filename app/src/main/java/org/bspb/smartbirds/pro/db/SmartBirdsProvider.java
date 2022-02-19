package org.bspb.smartbirds.pro.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by groupsky on 26.09.16.
 */

@ContentProvider(authority = SmartBirdsProvider.AUTHORITY, database = SmartBirdsDatabase.class)
public class SmartBirdsProvider {
    public static final String AUTHORITY = "org.bspb.smartbirds.pro.SmartBirdsProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String TYPE_LIST = "vnd.android.cursor.dir/";
    public static final String TYPE_ITEM = "vnd.android.cursor.item/";

    interface Path {
        String BY_ID = "id";
        String BY_TYPE = "type";
        String LIMIT = "limit";
        String MONITORINGS = "monitorings";
        String FORMS = "forms";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    static final String ENTRIES_COUNT = "(SELECT COUNT(*) FROM "
            + SmartBirdsDatabase.FORMS
            + " WHERE "
            + SmartBirdsDatabase.FORMS
            + "."
            + FormColumns.CODE
            + "="
            + SmartBirdsDatabase.MONITORINGS
            + "."
            + MonitoringColumns.CODE
            + ")";

    @TableEndpoint(table = SmartBirdsDatabase.MONITORINGS)
    public static class Monitorings {

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();
            map.put(MonitoringColumns.ENTRIES_COUNT, ENTRIES_COUNT);
            return map;
        }

        @ContentUri(
                path = Path.MONITORINGS,
                type = TYPE_LIST + Path.MONITORINGS,
                defaultSort = MonitoringColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MONITORINGS);

        @InexactContentUri(
                path = Path.MONITORINGS + "/*",
                name = Path.MONITORINGS + "_CODE",
                type = TYPE_ITEM + Path.MONITORINGS,
                whereColumn = MonitoringColumns.CODE,
                pathSegment = 1,
                defaultSort = MonitoringColumns._ID + " ASC")
        public static Uri withCode(String monitoringCode) {
            return buildUri(Path.MONITORINGS, monitoringCode);
        }
    }

    @TableEndpoint(table = SmartBirdsDatabase.FORMS)
    public static class Forms {
        @ContentUri(
                path = Path.FORMS,
                type = TYPE_LIST + Path.FORMS,
                defaultSort = FormColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FORMS);

        @InexactContentUri(
                path = Path.FORMS + "/#",
                name = Path.FORMS + "_ID",
                type = TYPE_ITEM + Path.FORMS,
                whereColumn = FormColumns._ID,
                pathSegment = 1,
                defaultSort = FormColumns._ID + " ASC")
        public static Uri withId(long id) {
            return buildUri(Path.FORMS, String.valueOf(id));
        }

        @InexactContentUri(
                path = Path.FORMS + "/monitoring/*",
                name = Path.FORMS + "_MONITORING_CODE",
                type = TYPE_LIST + Path.FORMS,
                whereColumn = FormColumns.CODE,
                pathSegment = 2,
                defaultSort = FormColumns._ID + " ASC")
        public static Uri withMonitoringCode(String monitoringCode) {
            return buildUri(Path.FORMS, "monitoring", monitoringCode);
        }

        @InexactContentUri(
                path = Path.FORMS + "/last_monitoring/*",
                name = Path.FORMS + "_LAST_MONITORING_CODE",
                type = TYPE_ITEM + Path.FORMS,
                whereColumn = FormColumns.CODE,
                pathSegment = 2,
                defaultSort = FormColumns._ID + " DESC",
                limit = "1"
        )

        private static void getNotifyUris(Context context, Uri uri, String where, String[] whereArgs, Collection<Uri> uris) {
            Set<String> monitoringCodes = new HashSet<>();
            Cursor c = context.getContentResolver().query(uri, new String[]{FormColumns._ID, FormColumns.CODE}, where, whereArgs, null);
            if (c != null) try {
                int idIdx = c.getColumnIndexOrThrow(FormColumns._ID);
                int codeIdx = c.getColumnIndexOrThrow(FormColumns.CODE);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    uris.add(withId(c.getLong(codeIdx)));
                    monitoringCodes.add(c.getString(codeIdx));
                }
            } finally {
                c.close();
            }
            for (String monitoringCode : monitoringCodes) {
                uris.add(withMonitoringCode(monitoringCode));
            }
        }

        @NotifyInsert(paths = {
                Path.FORMS,
                Path.FORMS + "/monitoring/*",
        })
        public static Uri[] onInsert(ContentValues contentValues) {
            LinkedList<Uri> notifyUris = new LinkedList<>();
            notifyUris.push(CONTENT_URI);
            if (contentValues.containsKey(FormColumns.CODE)) {
                notifyUris.push(withMonitoringCode(contentValues.getAsString(FormColumns.CODE)));
            }
            return notifyUris.toArray(new Uri[notifyUris.size()]);
        }

        @NotifyUpdate(paths = {
                Path.FORMS,
                Path.FORMS + "/#",
                Path.FORMS + "/monitoring/*",
        })
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs, ContentValues contentValues) {
            LinkedList<Uri> notifyUris = new LinkedList<>();
            notifyUris.push(CONTENT_URI);
            if (contentValues.containsKey(FormColumns.CODE)) {
                notifyUris.push(withMonitoringCode(contentValues.getAsString(FormColumns.CODE)));
            }
            getNotifyUris(context, uri, where, whereArgs, notifyUris);
            return notifyUris.toArray(new Uri[notifyUris.size()]);
        }

        @NotifyDelete(paths = {
                Path.FORMS + "/#",
                Path.FORMS + "/monitoring/*",
        })
        public static Uri[] onDelete(Context context, Uri uri) {
            LinkedList<Uri> notifyUris = new LinkedList<>();
            notifyUris.push(CONTENT_URI);
            getNotifyUris(context, uri, null, null, notifyUris);
            return notifyUris.toArray(new Uri[notifyUris.size()]);
        }
    }
}
