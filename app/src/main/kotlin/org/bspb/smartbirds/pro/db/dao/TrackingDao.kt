package org.bspb.smartbirds.pro.db.dao

import androidx.room.Dao
import androidx.room.Insert
import org.bspb.smartbirds.pro.db.model.Tracking

@Dao
interface TrackingDao {

    @Insert
    suspend fun insertTracking(tracking: Tracking)

}