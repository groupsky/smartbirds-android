package org.bspb.smartbirds.pro.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase
import org.bspb.smartbirds.pro.room.Tracking

class TrackingRepository {

    fun insertNewTracking(tracking: Tracking) {
        GlobalScope.launch(Dispatchers.IO) {
            SmartBirdsRoomDatabase.getInstance().trackingDao().insertTracking(tracking)
        }
    }
}