package org.bspb.smartbirds.pro.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.bspb.smartbirds.pro.room.dao.*
import org.bspb.smartbirds.pro.utils.debugLog

@Database(
    entities = [Form::class, MonitoringModel::class, NomenclatureModel::class, NomenclatureUsesCount::class, Tracking::class, ZoneModel::class],
    version = 6
)
abstract class SmartBirdsRoomDatabase : RoomDatabase() {

    abstract fun nomenclatureDao(): NomenclatureDao

    abstract fun nomenclatureUsesCountDao(): NomenclatureUsesCountDao

    abstract fun zoneDao(): ZoneDao

    abstract fun trackingDao(): TrackingDao

    abstract fun formDao(): FormDao

    abstract fun monitoringDao(): MonitoringDao

    companion object {
        @Volatile
        private var INSTANCE: SmartBirdsRoomDatabase? = null

        fun getInstance(): SmartBirdsRoomDatabase {
            checkNotNull(INSTANCE) { "Instance is null. init(Context context) must be called before getting the instace." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            // TODO load from bundled files on initial create
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartBirdsRoomDatabase::class.java,
                    "smartBirdsDatabaseRoom.db"
                )
                    .addMigrations(
                        MIGRATION_1_6
                    )
                    .build()
                INSTANCE = instance
                INSTANCE
            }
        }

        private val MIGRATION_1_6 = object : Migration(1, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                debugLog("In the migration")
                // Empty implementation, because the schema isn't changing.
            }

        }
    }
}