package org.bspb.smartbirds.pro.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by groupsky on 26.09.16.
 */

@Database(version = SmartBirdsDatabase.VERSION)
public class SmartBirdsDatabase {


    public static final int VERSION = 1;

    @Table(NomenclatureColumns.class)
    public static final String NOMENCLATURES = "nomenclatures";
}
