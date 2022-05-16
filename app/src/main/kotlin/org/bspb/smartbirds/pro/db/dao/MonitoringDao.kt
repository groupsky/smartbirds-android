package org.bspb.smartbirds.pro.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.db.model.MonitoringModel
import org.bspb.smartbirds.pro.db.model.MonitoringWithEntriesCount

@Dao
abstract class MonitoringDao {

    @Insert
    abstract suspend fun insertMonitoring(monitoring: MonitoringModel): Long

    @Update
    abstract suspend fun updateMonitoring(monitoring: MonitoringModel)

    @Query("DELETE FROM monitorings WHERE code = :code")
    abstract suspend fun deleteMonitoring(code: String)

    @Query("DELETE FROM forms WHERE code = :code")
    abstract suspend fun deleteMonitoringEntries(code: String)

    @Query("UPDATE monitorings SET status = :status WHERE code = :monitoringCode")
    abstract suspend fun updateStatus(monitoringCode: String, status: Monitoring.Status)

    @Query("SELECT * FROM monitorings WHERE code = :monitoringCode")
    abstract suspend fun findByCode(monitoringCode: String): MonitoringModel?

    @Query("SELECT * FROM monitorings WHERE code = :monitoringCode")
    abstract fun findByCodeLive(monitoringCode: String): LiveData<MonitoringModel?>

    @Query("SELECT * FROM monitorings WHERE status = 'wip' ORDER BY _id DESC")
    abstract suspend fun getActiveMonitoring(): MonitoringModel?

    @Query("SELECT * FROM monitorings WHERE status IN ('finished', 'paused', 'uploaded') ORDER BY _id DESC")
    abstract suspend fun getLastMonitoring(): MonitoringModel?

    @Query("SELECT * FROM monitorings WHERE status = 'paused' ORDER BY _id DESC")
    abstract suspend fun getPausedMonitoring(): MonitoringModel?

    @Query("SELECT code FROM monitorings WHERE status = :status")
    abstract suspend fun getMonitoringCodesForStatus(status: Monitoring.Status): List<String>

    @Query("SELECT COUNT(code) FROM monitorings WHERE status = :status")
    abstract suspend fun countMonitoringsForStatus(status: Monitoring.Status): Int

    @Query("SELECT COUNT(code) FROM monitorings WHERE status = :status")
    abstract fun countMonitoringsForStatusLive(status: Monitoring.Status): LiveData<Int>

    @Query(
        """
            SELECT 
                m.*, COUNT(f._id) as records 
            FROM 
                monitorings as m 
            JOIN forms as f ON m.code = f.code
            GROUP BY 
                f.code
            HAVING
                (records > 0 OR m.status <> 'canceled')
                AND (m.status = :status OR :status IS NULL)
        """
    )
    abstract fun getMonitoringsWithEntriesCount(status: Monitoring.Status?): LiveData<List<MonitoringWithEntriesCount>>

    @Transaction
    open suspend fun deleteMonitoringAndEntries(code: String) {
        deleteMonitoring(code)
        deleteMonitoringEntries(code)
    }
}