package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.BLOB;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by groupsky on 22.11.16.
 */

public interface FormColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(TEXT)
    @NotNull
    String CODE = "code";

    @DataType(TEXT)
    @NotNull
    String TYPE = "type";

    @DataType(REAL)
    @NotNull
    String LATITUDE = "latitude";

    @DataType(REAL)
    @NotNull
    String LONGITUDE = "longitude";

    @DataType(BLOB)
    @NotNull
    String DATA = "data";

}
