package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.BLOB;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by groupsky on 27.01.17.
 */

public interface LocationColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(REAL)
    String LAT = "lat";

    @DataType(REAL)
    String LON = "lon";

    @DataType(BLOB)
    String DATA = "data";

    // calculated
    String DISTANCE = "distance";
}
