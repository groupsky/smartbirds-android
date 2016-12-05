package org.bspb.smartbirds.pro.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import static org.bspb.smartbirds.pro.ui.utils.Configuration.MAX_RECENT_USED_VALUES;

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
        String LOCATIONS = "locations";
        String NOMENCLATURE_USES_COUNT = "nomenclature_uses_count";
        String NOMENCLATURES = "nomenclatures";
        String ZONES = "zones";
        String MONITORINGS = "monitorings";
        String FORMS = "forms";
        String TRACKING = "tracking";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = SmartBirdsDatabase.LOCATIONS)
    public static class Locations {
        @ContentUri(
                path = Path.LOCATIONS,
                type = TYPE_LIST + Path.LOCATIONS,
                defaultSort = LocationColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.LOCATIONS);

        @ContentUri(
                path = Path.LOCATIONS + "/" + Path.LIMIT + "/1",
                type = TYPE_ITEM + Path.LOCATIONS,
                defaultSort = LocationColumns._ID + " ASC",
                limit = "1")
        public static final Uri CONTENT_URI_LIMIT_1 = buildUri(Path.LOCATIONS);

        @InexactContentUri(
                path = Path.LOCATIONS + "/#",
                name = Path.LOCATIONS + "_ID",
                type = TYPE_ITEM + Path.LOCATIONS,
                whereColumn = LocationColumns._ID,
                pathSegment = 1)
        public static Uri byId(long id) {
            return buildUri(Path.LOCATIONS, String.valueOf(id));
        }

        public static String[] distance(double lat, double lon) {
            return new String[]{
                    "(lat - " + lat + ")*(lat- " + lat + ") + " +
                            "(lon - " + lon + ")*(lon - " + lon + ") as "+LocationColumns.DISTANCE};
        }
    }

    @TableEndpoint(table = SmartBirdsDatabase.NOMENCLATURES)
    public static class Nomenclatures {

        @ContentUri(
                path = Path.NOMENCLATURES,
                type = TYPE_LIST + Path.NOMENCLATURES,
                defaultSort = NomenclatureColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.NOMENCLATURES);

        @InexactContentUri(
                path = Path.NOMENCLATURES + "/*",
                name = Path.NOMENCLATURES + "_TYPE",
                type = TYPE_LIST + Path.NOMENCLATURES,
                whereColumn = NomenclatureColumns.TYPE,
                pathSegment = 1,
                defaultSort = NomenclatureColumns._ID + " ASC")
        public static Uri withType(String type) {
            return buildUri(Path.NOMENCLATURES, type);
        }
    }

    @TableEndpoint(table = SmartBirdsDatabase.NOMENCLATURE_USES_COUNT)
    public static class NomenclatureUsesCount {
        @ContentUri(
                path = Path.NOMENCLATURE_USES_COUNT,
                type = TYPE_LIST + Path.NOMENCLATURE_USES_COUNT,
                defaultSort = NomenclatureUsesCountColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.NOMENCLATURE_USES_COUNT);

        @InexactContentUri(
                path = Path.NOMENCLATURE_USES_COUNT + "/" + Path.BY_ID + "/#",
                name = Path.NOMENCLATURE_USES_COUNT + "_ID",
                type = TYPE_ITEM + Path.NOMENCLATURE_USES_COUNT,
                whereColumn = NomenclatureUsesCountColumns._ID,
                pathSegment = 2)
        public static Uri forId(long id) {
            return buildUri(Path.NOMENCLATURE_USES_COUNT, Path.BY_ID, String.valueOf(id));
        }

        @InexactContentUri(
                path = Path.NOMENCLATURE_USES_COUNT + "/" + Path.BY_TYPE + "/*",
                name = Path.NOMENCLATURE_USES_COUNT + "_TYPE",
                type = TYPE_LIST + Path.NOMENCLATURE_USES_COUNT,
                whereColumn = NomenclatureUsesCountColumns.TYPE,
                pathSegment = 2,
                defaultSort = NomenclatureUsesCountColumns.COUNT + " DESC",
                limit = "" + MAX_RECENT_USED_VALUES)
        public static Uri forType(String type) {
            return buildUri(Path.NOMENCLATURE_USES_COUNT, Path.BY_TYPE, type);
        }
    }
    
    @TableEndpoint(table = SmartBirdsDatabase.ZONES)
    public static class Zones {
        @ContentUri(
                path = Path.ZONES,
                type = TYPE_LIST + Path.ZONES,
                defaultSort = ZoneColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.ZONES);
    }

    @TableEndpoint(table = SmartBirdsDatabase.MONITORINGS)
    public static class Monitorings {
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
                pathSegment = 1,
                defaultSort = FormColumns._ID + " ASC")
        public static Uri withMonitoringCode(String monitoringCode) {
            return buildUri(Path.FORMS, "monitoring", monitoringCode);
        }

        @InexactContentUri(
                path = Path.FORMS + "/last_monitoring/*",
                name = Path.FORMS + "_LAST_MONITORING_CODE",
                type = TYPE_ITEM + Path.FORMS,
                whereColumn = FormColumns.CODE,
                pathSegment = 1,
                defaultSort = FormColumns._ID + " DESC",
                limit = "1"
            )
        public static Uri lastWithMonitoringCode(String monitoringCode) {
            return buildUri(Path.FORMS, "last_monitoring", monitoringCode);
        }
    }

    @TableEndpoint(table = SmartBirdsDatabase.TRACKING)
    public static class Tracking {
        @ContentUri(
                path = Path.TRACKING,
                type = TYPE_LIST + Path.TRACKING,
                defaultSort = TrackingColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.TRACKING);

        @InexactContentUri(
                path = Path.TRACKING + "/#",
                name = Path.TRACKING + "_ID",
                type = TYPE_ITEM + Path.TRACKING,
                whereColumn = TrackingColumns._ID,
                pathSegment = 1,
                defaultSort = TrackingColumns._ID + " ASC")
        public static Uri withId(long id) {
            return buildUri(Path.TRACKING, String.valueOf(id));
        }

        @InexactContentUri(
                path = Path.TRACKING + "/monitoring/*",
                name = Path.TRACKING + "_MONITORING_CODE",
                type = TYPE_LIST + Path.TRACKING,
                whereColumn = TrackingColumns.CODE,
                pathSegment = 1,
                defaultSort = TrackingColumns._ID + " ASC")
        public static Uri withMonitoringCode(String monitoringCode) {
            return buildUri(Path.TRACKING, "monitoring", monitoringCode);
        }
    }
}
