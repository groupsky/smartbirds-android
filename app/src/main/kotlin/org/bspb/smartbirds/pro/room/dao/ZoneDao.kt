package org.bspb.smartbirds.pro.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.bspb.smartbirds.pro.room.model.ZoneModel

@Dao
abstract class ZoneDao {

    @Query("DELETE FROM zones")
    abstract fun clearTable()

    @Insert
    abstract fun insertAll(zones: List<ZoneModel>)

    @Transaction
    open fun updateZonesAndClearOld(zones: List<ZoneModel>) {
        clearTable()
        insertAll(zones)
    }

    @Query("SELECT * FROM zones ORDER BY _id ASC")
    abstract fun getAll(): LiveData<List<ZoneModel>>
}