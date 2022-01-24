package org.bspb.smartbirds.pro.room.dao

import androidx.room.Dao
import androidx.room.Insert
import org.bspb.smartbirds.pro.room.Tracking

@Dao
interface TrackingDao {

    @Insert
    suspend fun insertTracking(tracking: Tracking)

}