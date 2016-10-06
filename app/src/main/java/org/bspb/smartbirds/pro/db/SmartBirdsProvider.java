package org.bspb.smartbirds.pro.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

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
}
