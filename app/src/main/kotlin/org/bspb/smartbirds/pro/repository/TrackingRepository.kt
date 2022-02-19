package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase
import org.bspb.smartbirds.pro.room.Tracking

class TrackingRepository {

    suspend fun insertNewTracking(tracking: Tracking) {
        SmartBirdsRoomDatabase.getInstance().trackingDao().insertTracking(tracking)
    }
}