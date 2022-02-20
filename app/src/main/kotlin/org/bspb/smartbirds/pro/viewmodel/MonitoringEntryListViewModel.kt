package org.bspb.smartbirds.pro.viewmodel

import androidx.lifecycle.*
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.repository.FormRepository
import org.bspb.smartbirds.pro.room.Form
import org.bspb.smartbirds.pro.utils.MonitoringManagerNew

class MonitoringEntryListViewModel : ViewModel() {

    private val formRepository = FormRepository()
    private var monitoringCode: String? = null
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
}