package org.bspb.smartbirds.pro.room.dao

import androidx.room.*
import org.bspb.smartbirds.pro.room.MonitoringModel

@Dao
abstract class MonitoringDao {

    @Insert
    abstract suspend fun insertMonitoring(monitoring: MonitoringModel): Long

    @Update
    abstract suspend fun updateMonitoring(monitoring: MonitoringModel)

    @Query("SELECT * FROM monitorings WHERE code = :code ORDER BY _id ASC")
    abstract suspend fun findByCode(code: String): MonitoringModel

    @Query("DELETE FROM monitorings WHERE code = :code")
    abstract suspend fun deleteMonitoring(code: String)

    @Query("DELETE FROM forms WHERE code = :code")
    abstract suspend fun deleteMonitoringEntries(code: String)

    @Transaction
    open suspend fun deleteMonitoringAndEntries(code: String) {
        deleteMonitoring(code)
        deleteMonitoringEntries(code)
    }

    @Transaction
    open suspend fun updateMonitoringByCode(monitoring: MonitoringModel) {
        var dbModel = monitoring?.code?.let { findByCode(it) }
        if (dbModel != null) {
            updateMonitoring(monitoring.copy(id = dbModel.id))
        }
    }


}