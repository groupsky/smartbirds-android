package org.bspb.smartbirds.pro.viewmodel

import androidx.lifecycle.*
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.repository.MonitoringRepository
import org.bspb.smartbirds.pro.db.model.MonitoringWithEntriesCount
import org.bspb.smartbirds.pro.utils.MonitoringManager

class MonitoringListViewModel : ViewModel() {

    private val monitoringRepository = MonitoringRepository()
    private var filterStatus: MutableLiveData<Monitoring.Status?> = MutableLiveData(null)
    var entries: LiveData<List<Monitoring>>? = null


    fun init() {
        if (entries != null) {
            return
        }
        this.entries = filterStatus.switchMap { status ->
            monitoringRepository.getMonitoringsWithEntriesCount(status).switchMap { items ->
                transformDbEntries(items)
            }
        }
    }

    private fun transformDbEntries(items: List<MonitoringWithEntriesCount>?): LiveData<List<Monitoring>> {
        return liveData {
            val monitorings = mutableListOf<Monitoring>()

            items?.forEach {
                val monitoring = MonitoringManager.monitoringFromDb(it.monitoring)
                monitoring!!.entriesCount = it.records
                monitorings.add(monitoring!!)
            }

            emit(monitorings)
        }
    }

    fun setFilterStatus(status: Monitoring.Status?) {
        filterStatus.value = status
    }
}