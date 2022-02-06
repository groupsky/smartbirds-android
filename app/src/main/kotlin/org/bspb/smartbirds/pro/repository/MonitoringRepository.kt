package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase

class MonitoringRepository {

    suspend fun deleteMonitoring(monitoringCode: String) {
        SmartBirdsRoomDatabase.getInstance().monitoringDao().deleteMonitoringAndEntries(monitoringCode)
    }

}