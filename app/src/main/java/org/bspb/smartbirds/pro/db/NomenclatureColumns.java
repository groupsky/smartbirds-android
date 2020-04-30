package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.BLOB;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by groupsky on 27.09.16.
 */

public interface NomenclatureColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(TEXT)
    String TYPE = "type";

    @DataType(TEXT)
    String LABEL_BG = "label_bg";

    @DataType(TEXT)
    String LABEL_EN = "label_en";

    @DataType(BLOB)
    String DATA = "data";

}
