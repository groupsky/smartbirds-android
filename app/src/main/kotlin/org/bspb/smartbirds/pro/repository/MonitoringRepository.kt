package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.room.MonitoringModel
import org.bspb.smartbirds.pro.room.MonitoringWithEntriesCount
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase

class MonitoringRepository {

    suspend fun deleteMonitoring(monitoringCode: String) {
        SmartBirdsRoomDatabase.getInstance().monitoringDao().deleteMonitoringAndEntries(monitoringCode)
    }

    suspend fun createMonitoring(monitoringModel: MonitoringModel): Long {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().insertMonitoring(monitoringModel)
    }

    suspend fun updateMonitoring(monitoringModel: MonitoringModel) {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().updateMonitoring(monitoringModel)
    }

    suspend fun updateStatus(monitoringCode: String, status: Monitoring.Status) {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().updateStatus(monitoringCode, status)
    }

    suspend fun getMonitoring(monitoringCode: String): MonitoringModel? {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().findByCode(monitoringCode)
    }

    fun getMonitoringLive(monitoringCode: String): LiveData<MonitoringModel?> {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().findByCodeLive(monitoringCode)
    }

    suspend fun getActiveMonitoring(): MonitoringModel? {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().getActiveMonitoring()
    }

    suspend fun getPausedMonitoring(): MonitoringModel? {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().getPausedMonitoring()
    }

    suspend fun getMonitoringCodesForStatus(status: Monitoring.Status): List<String> {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().getMonitoringCodesForStatus(status)
    }

    suspend fun countMonitoringsForStatus(status: Monitoring.Status): Int {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().countMonitoringsForStatus(status)
    }

    fun getMonitoringsWithEntriesCount(status: Monitoring.Status?): LiveData<List<MonitoringWithEntriesCount>> {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().getMonitoringsWithEntriesCount(status)
    }
}