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


    public static final int VERSION = 6;

    @Table(FormColumns.class)
    public static final String FORMS = "forms";

    @Table(LocationColumns.class)
    public static final String LOCATIONS = "locations";

    @Table(MonitoringColumns.class)
    public static final String MONITORINGS = "monitorings";

    @Table(NomenclatureColumns.class)
    public static final String NOMENCLATURES = "nomenclatures";

    @Table(TrackingColumns.class)
    public static final String TRACKING = "tracking";

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
                    break;
                case 2:
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.NOMENCLATURE_USES_COUNT);
                    break;
                case 3:
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.LOCATIONS);
                    break;
                case 4:
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.MONITORINGS);
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.FORMS);
                    db.execSQL(org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase.TRACKING);
                    break;
                case 5:
                    db.execSQL("ALTER TABLE nomenclatures ADD COLUMN data BLOB");
                    db.execSQL("ALTER TABLE nomenclature_uses_count ADD COLUMN data BLOB");
                    db.execSQL("DELETE FROM nomenclature_uses_count");
                    db.execSQL("ALTER TABLE nomenclature_uses_count ADD COLUMN label_id TEXT");
                    break;
            }
        }
    }
}
