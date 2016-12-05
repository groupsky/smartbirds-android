package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by groupsky on 05.12.16.
 */

public interface TrackingColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(TEXT)
    @NotNull
    String CODE = "code";

    @DataType(INTEGER)
    @NotNull
    String TIME = "time";

    @DataType(REAL)
    @NotNull
    String LATITUDE = "latitude";

    @DataType(REAL)
    @NotNull
    String LONGITUDE = "longitude";

    @DataType(REAL)
    String ALTITUDE = "altutude";

}
