package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.db.SmartBirdsDatabase
import org.bspb.smartbirds.pro.db.model.ZoneModel

class ZoneRepository {

    fun getAllZones(): LiveData<List<ZoneModel>> {
        return SmartBirdsDatabase.getInstance().zoneDao().getAll()
    }

}