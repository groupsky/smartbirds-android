package org.bspb.smartbirds.pro.room.dao

import androidx.room.*
import org.bspb.smartbirds.pro.room.MonitoringModel

@Dao
abstract class MonitoringDao {

    @Insert
    abstract suspend fun insertMonitoring(monitoring: MonitoringModel): Long

    @Query("DELETE FROM monitorings WHERE code = :code")
    abstract suspend fun deleteMonitoring(code: String)

    @Query("DELETE FROM forms WHERE code = :code")
    abstract suspend fun deleteMonitoringEntries(code: String)

    @Transaction
    open suspend fun deleteMonitoringAndEntries(code: String) {
        deleteMonitoring(code)
        deleteMonitoringEntries(code)
    }
}