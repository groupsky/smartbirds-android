package org.bspb.smartbirds.pro.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.bspb.smartbirds.pro.room.Nomenclature
import org.bspb.smartbirds.pro.room.Zone

@Dao
abstract class ZoneDao {

    @Query("DELETE FROM zones")
    abstract fun clearTable()

    @Insert
    abstract fun insertAll(zones: List<Zone>)

    @Transaction
    open fun updateZonesAndClearOld(zones: List<Zone>) {
        clearTable()
        insertAll(zones)
    }
}