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
        String NOMENCLATURES = "nomenclatures";
        String ZONES = "zones";
        String NOMENCLATURE_USES_COUNT = "nomenclature_uses_count";
        String BY_ID = "id";
        String BY_TYPE = "type";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
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

    @TableEndpoint(table = SmartBirdsDatabase.ZONES)
    public static class Zones {
        @ContentUri(
                path = Path.ZONES,
                type = TYPE_LIST + Path.ZONES,
                defaultSort = ZoneColumns._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.ZONES);
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
}
