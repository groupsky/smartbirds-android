package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.BLOB;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by groupsky on 06.10.16.
 */

public interface ZoneColumns {

    @DataType(TEXT)
    @PrimaryKey
    String _ID = BaseColumns._ID;

    @DataType(INTEGER)
    String LOCATION_ID = "location_id";

    @DataType(BLOB)
    String DATA = "data";

}
