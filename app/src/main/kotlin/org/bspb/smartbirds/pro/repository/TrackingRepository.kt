package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.room.SmartBirdsDatabase
import org.bspb.smartbirds.pro.room.model.Tracking

class TrackingRepository {

    suspend fun insertNewTracking(tracking: Tracking) {
        SmartBirdsDatabase.getInstance().trackingDao().insertTracking(tracking)
    }
}