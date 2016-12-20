package org.bspb.smartbirds.pro.db;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import org.bspb.smartbirds.pro.backend.dto.Nomenclature;

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
    String TYPE = "type";

    @DataType(TEXT)
    String LABEL_BG = "label_bg";

    @DataType(TEXT)
    String LABEL_EN = "label_en";

    @DataType(INTEGER)
    String COUNT = "count";

}
