package org.bspb.smartbirds.pro.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

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
}
