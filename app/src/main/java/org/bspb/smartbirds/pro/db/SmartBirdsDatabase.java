package org.bspb.smartbirds.pro.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean;
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean_;

/**
 * Created by groupsky on 26.09.16.
 */

@Database(version = SmartBirdsDatabase.VERSION)
public class SmartBirdsDatabase {


    public static final int VERSION = 2;

    @Table(NomenclatureColumns.class)
    public static final String NOMENCLATURES = "nomenclatures";

    @Table(ZoneColumns.class)
    public static final String ZONES = "zones";

    @Table(NomenclatureUsesCountColumns.class)
    public static final String NOMENCLATURE_USES_COUNT = "nomenclature_uses_count";

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
        NomenclaturesBean nomenclatureBean = NomenclaturesBean_.getInstance_(context);
        for (ContentValues cv : nomenclatureBean.prepareNomenclatureCV(nomenclatureBean.loadBundledNomenclatures())) {
            db.insert(NOMENCLATURES, null, cv);
        }
        for (ContentValues cv : nomenclatureBean.prepareSpeciesCV(nomenclatureBean.loadBundledSpecies())) {
            db.insert(NOMENCLATURES, null, cv);
        }
    }

    @OnUpgrade
    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (; oldVersion < newVersion; oldVersion++) {
            switch (oldVersion) {
                case 1:
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.ZONES);
                    db.execSQL(org.bspb.smartbirds.pro.db.generated
                            .SmartBirdsDatabase.NOMENCLATURE_USES_COUNT);
                    break;

            }
        }
    }
}
