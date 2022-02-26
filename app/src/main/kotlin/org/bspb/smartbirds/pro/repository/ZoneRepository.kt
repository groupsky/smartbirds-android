package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.room.SmartBirdsDatabase
import org.bspb.smartbirds.pro.room.model.ZoneModel

class ZoneRepository {

    fun getAllZones(): LiveData<List<ZoneModel>> {
        return SmartBirdsDatabase.getInstance().zoneDao().getAll()
    }

}