package org.bspb.smartbirds.pro.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MonitoringDao {

    @Delete
    abstract suspend fun deleteMonitoring(code: String)

    @Query("DELETE FROM forms WHERE code = :code")
    abstract suspend fun deleteMonitoringEntries(code: String)

    @Transaction
    suspend fun deleteMonitoringAndEntries(code: String) {
        deleteMonitoring(code)
        deleteMonitoringEntries(code)
    }
}