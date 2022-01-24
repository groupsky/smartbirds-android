package org.bspb.smartbirds.pro.viewmodel

import androidx.lifecycle.*
import org.bspb.smartbirds.pro.backend.dto.Zone
import org.bspb.smartbirds.pro.repository.ZoneRepository

class MonitoringViewModel : ViewModel() {

    private val repo = ZoneRepository()
    private var zones: LiveData<List<Zone>>? = null

    fun getZones(): LiveData<List<Zone>> {
        if (zones != null) {
            return zones!!
        }
        zones = Transformations.switchMap(repo.getAllZones()) { dbZones ->
            val zones = mutableListOf<Zone>()
            dbZones.forEach { dbZone ->
                zones.add(Zone.fromDbModel(dbZone))
            }
            return@switchMap MutableLiveData<List<Zone>>(zones)
        }

        return zones!!
    }
}