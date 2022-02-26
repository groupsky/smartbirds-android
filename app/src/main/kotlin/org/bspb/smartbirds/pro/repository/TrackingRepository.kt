package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.db.SmartBirdsDatabase
import org.bspb.smartbirds.pro.db.model.Tracking

class TrackingRepository {

    suspend fun insertNewTracking(tracking: Tracking) {
        SmartBirdsDatabase.getInstance().trackingDao().insertTracking(tracking)
    }
}