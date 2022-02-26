package org.bspb.smartbirds.pro.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.bspb.smartbirds.pro.room.dao.*
import org.bspb.smartbirds.pro.room.model.*
import org.bspb.smartbirds.pro.utils.debugLog

@Database(
    entities = [Form::class, MonitoringModel::class, NomenclatureModel::class, NomenclatureUsesCount::class, Tracking::class, ZoneModel::class],
    version = 7
)
@TypeConverters(Converters::class)
abstract class SmartBirdsDatabase : RoomDatabase() {

    abstract fun nomenclatureDao(): NomenclatureDao

    abstract fun nomenclatureUsesCountDao(): NomenclatureUsesCountDao

    abstract fun zoneDao(): ZoneDao

    abstract fun trackingDao(): TrackingDao

    abstract fun formDao(): FormDao

    abstract fun monitoringDao(): MonitoringDao

    companion object {
        @Volatile
        private var INSTANCE: SmartBirdsDatabase? = null

        fun getInstance(): SmartBirdsDatabase {
            checkNotNull(INSTANCE) { "Instance is null. init(Context context) must be called before getting the instace." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartBirdsDatabase::class.java,
                    "smartBirdsDatabase.db"
                )
                    .addMigrations(
                        MIGRATION_1_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7
                    )
                    .build()
                INSTANCE = instance
                INSTANCE
            }
        }

        private val MIGRATION_1_5 = object : Migration(1, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                debugLog("In the migration")
                // Empty implementation, because the schema isn't changing.
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE nomenclatures ADD COLUMN data BLOB")
                database.execSQL("ALTER TABLE nomenclature_uses_count ADD COLUMN data BLOB")
                database.execSQL("DELETE FROM nomenclature_uses_count")
                database.execSQL("ALTER TABLE nomenclature_uses_count ADD COLUMN label_id TEXT")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create backup tables
                database.execSQL("CREATE TABLE forms_backup (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,code TEXT NOT NULL,type TEXT NOT NULL,latitude REAL NOT NULL,longitude REAL NOT NULL,data BLOB NOT NULL)")
                database.execSQL("CREATE TABLE monitorings_backup (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,code TEXT NOT NULL UNIQUE,status TEXT NOT NULL,data BLOB NOT NULL)")
                database.execSQL("CREATE TABLE nomenclature_uses_count_backup (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,type TEXT,label_id TEXT,data BLOB,count INTEGER)")
                database.execSQL("CREATE TABLE nomenclatures_backup (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,type TEXT,data BLOB)")
                database.execSQL("CREATE TABLE zones_backup (_id TEXT PRIMARY KEY NOT NULL,location_id INTEGER,data BLOB)")

                // Move data to backup tables
                database.execSQL("INSERT INTO forms_backup (_id, code, type, latitude, longitude, data) SELECT _id, code, type, latitude, longitude, data FROM forms")
                database.execSQL("INSERT INTO monitorings_backup (_id, code, status, data) SELECT _id, code, status, data FROM monitorings")
                database.execSQL("INSERT INTO nomenclature_uses_count_backup (_id, type, label_id, data, count) SELECT _id, type, label_id, data, count FROM nomenclature_uses_count")
                database.execSQL("INSERT INTO nomenclatures_backup (_id, type, data) SELECT _id,type,data FROM nomenclatures")
                database.execSQL("INSERT INTO zones_backup (_id, location_id, data) SELECT _id, location_id, data FROM zones")

                // DROP original tables
                database.execSQL("DROP TABLE forms")
                database.execSQL("DROP TABLE locations")
                database.execSQL("DROP TABLE monitorings")
                database.execSQL("DROP TABLE nomenclature_uses_count")
                database.execSQL("DROP TABLE nomenclatures")
                database.execSQL("DROP TABLE tracking")
                database.execSQL("DROP TABLE zones")

                // CREATE new tables to match the models
                database.execSQL("CREATE TABLE tracking (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,code TEXT,time INTEGER,latitude REAL,longitude REAL,altitude REAL)")

                // Rename backup tables
                database.execSQL("ALTER TABLE forms_backup RENAME TO forms")
                database.execSQL("ALTER TABLE monitorings_backup RENAME TO monitorings")
                database.execSQL("ALTER TABLE nomenclature_uses_count_backup RENAME TO nomenclature_uses_count")
                database.execSQL("ALTER TABLE nomenclatures_backup RENAME TO nomenclatures")
                database.execSQL("ALTER TABLE zones_backup RENAME TO zones")
            }
        }


    }
}