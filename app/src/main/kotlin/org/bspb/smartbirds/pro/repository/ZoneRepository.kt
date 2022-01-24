package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase
import org.bspb.smartbirds.pro.room.ZoneModel

class ZoneRepository {

    fun getAllZones(): LiveData<List<ZoneModel>> {
        return SmartBirdsRoomDatabase.getInstance().zoneDao().getAll()
    }

}