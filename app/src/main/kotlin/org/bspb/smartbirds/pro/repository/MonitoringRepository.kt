package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.room.MonitoringModel
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase

class MonitoringRepository {

    suspend fun deleteMonitoring(monitoringCode: String) {
        SmartBirdsRoomDatabase.getInstance().monitoringDao()
            .deleteMonitoringAndEntries(monitoringCode)
    }

    suspend fun insertMonitoring(monitoring: MonitoringModel): Long {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().insertMonitoring(monitoring)
    }

    suspend fun updateMonitoring(monitoring: MonitoringModel) {
        return SmartBirdsRoomDatabase.getInstance().monitoringDao().updateMonitoringByCode(monitoring)
    }
}