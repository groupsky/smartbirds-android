package org.bspb.smartbirds.pro.viewmodel

import androidx.lifecycle.*
import org.bspb.smartbirds.pro.backend.dto.Zone
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.repository.FormRepository
import org.bspb.smartbirds.pro.repository.ZoneRepository
import org.bspb.smartbirds.pro.room.Form
import org.bspb.smartbirds.pro.utils.MonitoringManagerNew

class MonitoringViewModel : ViewModel() {

    private val formRepository = FormRepository()
    private val zonesRepo = ZoneRepository()
    private var zones: LiveData<List<Zone>>? = null
    var monitoringCode: String? = null
    var entries: LiveData<List<MonitoringEntry>>? = null

    fun init(monitoringCode: String?) {
        if (entries != null) {
            return
        }
        this.monitoringCode = monitoringCode
        this.entries = formRepository.findAllByMonitoringCode(monitoringCode!!).switchMap { transformDbEntries(it) }
    }

    private fun transformDbEntries(items: List<Form>?): LiveData<List<MonitoringEntry>> {
        return liveData {
            var monitoringEntries = mutableListOf<MonitoringEntry>()

            items?.forEach {
                monitoringEntries.add(MonitoringManagerNew.entryFromDb(it))
            }

            emit(monitoringEntries)
        }
    }

    fun getZones(): LiveData<List<Zone>> {
        if (zones != null) {
            return zones!!
        }
        zones = Transformations.switchMap(zonesRepo.getAllZones()) { dbZones ->
            val zones = mutableListOf<Zone>()
            dbZones.forEach { dbZone ->
                zones.add(Zone.fromDbModel(dbZone))
            }
            return@switchMap MutableLiveData<List<Zone>>(zones)
        }

        return zones!!
    }
}