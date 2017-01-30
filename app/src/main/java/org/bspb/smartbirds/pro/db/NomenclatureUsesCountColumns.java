package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by Ilian Georgiev.
 */

public interface NomenclatureUsesCountColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(TEXT)
    String TYPE = NomenclatureColumns.TYPE;

    @DataType(TEXT)
    String LABEL_BG = NomenclatureColumns.LABEL_BG;

    @DataType(TEXT)
    String LABEL_EN = NomenclatureColumns.LABEL_EN;

    @DataType(INTEGER)
    String COUNT = "count";
}
