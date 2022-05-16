package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.db.model.MonitoringModel
import org.bspb.smartbirds.pro.db.model.MonitoringWithEntriesCount
import org.bspb.smartbirds.pro.db.SmartBirdsDatabase

class MonitoringRepository {

    suspend fun deleteMonitoring(monitoringCode: String) {
        SmartBirdsDatabase.getInstance().monitoringDao().deleteMonitoringAndEntries(monitoringCode)
    }

    suspend fun createMonitoring(monitoringModel: MonitoringModel): Long {
        return SmartBirdsDatabase.getInstance().monitoringDao().insertMonitoring(monitoringModel)
    }

    suspend fun updateMonitoring(monitoringModel: MonitoringModel) {
        return SmartBirdsDatabase.getInstance().monitoringDao().updateMonitoring(monitoringModel)
    }

    suspend fun updateStatus(monitoringCode: String, status: Monitoring.Status) {
        return SmartBirdsDatabase.getInstance().monitoringDao().updateStatus(monitoringCode, status)
    }

    suspend fun getMonitoring(monitoringCode: String): MonitoringModel? {
        return SmartBirdsDatabase.getInstance().monitoringDao().findByCode(monitoringCode)
    }

    fun getMonitoringLive(monitoringCode: String): LiveData<MonitoringModel?> {
        return SmartBirdsDatabase.getInstance().monitoringDao().findByCodeLive(monitoringCode)
    }

    suspend fun getActiveMonitoring(): MonitoringModel? {
        return SmartBirdsDatabase.getInstance().monitoringDao().getActiveMonitoring()
    }

    suspend fun getLastMonitoring(): MonitoringModel? {
        return SmartBirdsDatabase.getInstance().monitoringDao().getLastMonitoring()
    }

    suspend fun getPausedMonitoring(): MonitoringModel? {
        return SmartBirdsDatabase.getInstance().monitoringDao().getPausedMonitoring()
    }

    suspend fun getMonitoringCodesForStatus(status: Monitoring.Status): List<String> {
        return SmartBirdsDatabase.getInstance().monitoringDao().getMonitoringCodesForStatus(status)
    }

    suspend fun countMonitoringsForStatus(status: Monitoring.Status): Int {
        return SmartBirdsDatabase.getInstance().monitoringDao().countMonitoringsForStatus(status)
    }

    fun countMonitoringsForStatusLive(status: Monitoring.Status): LiveData<Int> {
        return SmartBirdsDatabase.getInstance().monitoringDao().countMonitoringsForStatusLive(status)
    }

    fun getMonitoringsWithEntriesCount(status: Monitoring.Status?): LiveData<List<MonitoringWithEntriesCount>> {
        return SmartBirdsDatabase.getInstance().monitoringDao().getMonitoringsWithEntriesCount(status)
    }
}