package org.bspb.smartbirds.pro.viewmodel

import android.content.Context
import androidx.lifecycle.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.db.model.Form
import org.bspb.smartbirds.pro.dto.MonitoringReportEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.enums.ReportEntryType
import org.bspb.smartbirds.pro.repository.FormRepository
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.utils.debugLog

class MonitoringReportViewModel : ViewModel() {

    var monitoringCode: String? = null
    private val formRepository = FormRepository()
    var entries: LiveData<List<MonitoringEntry>>? = null

    fun init(monitoringCode: String?) {
        if (entries != null) {
            return
        }
        this.monitoringCode = monitoringCode
        this.entries = formRepository.findAllByMonitoringCode(monitoringCode!!).distinctUntilChanged()
            .switchMap { transformDbEntries(it) }
    }

    private fun transformDbEntries(items: List<Form>?): LiveData<List<MonitoringEntry>> {
        return liveData {
            var monitoringEntries = mutableListOf<MonitoringEntry>()

            items?.forEach {
                monitoringEntries.add(MonitoringManager.entryFromDb(it))
            }

            emit(monitoringEntries)
        }
    }

    fun prepareReportEntries(context: Context, items: List<MonitoringEntry>): MutableList<MonitoringReportEntry> {
        var monitoringReportForms = mutableMapOf<String, MutableList<MonitoringEntry>>()
        var reportEntries = mutableListOf<MonitoringReportEntry>()

        items.forEach { monitoringEntry ->
            if (!monitoringReportForms.containsKey(monitoringEntry.type.name)) {
                monitoringReportForms[monitoringEntry.type.name] = mutableListOf()
            }
            monitoringReportForms[monitoringEntry.type.name]?.add(monitoringEntry)
        }

        val sortedFormTypes = monitoringReportForms.keys.sortedWith { o1, o2 ->
            context.getString(EntryType.valueOf(o1).titleId)
                .compareTo(context.getString(EntryType.valueOf(o2).titleId))
        }

        sortedFormTypes.forEach { formType ->
            reportEntries.add(
                MonitoringReportEntry(
                    context.getString(EntryType.valueOf(formType).titleId),
                    monitoringReportForms[formType]!!.size,
                    ReportEntryType.header
                )
            )
            val formEntries = mutableMapOf<String, Int>()
            monitoringReportForms[formType]?.forEach { entry ->
                var label: String? = "N/A"
                label = if (entry.type == EntryType.PYLONS) {
                    entry.data[context.getString(R.string.tag_pylons_pylon_type)]
                } else if (entry.type == EntryType.CICONIA) {
                    context.getString(entry.type.titleId)
                } else {
                    entry.data[context.getString(R.string.tag_species_scientific_name)] ?: entry.data[context.getString(
                        R.string.tag_observed_bird
                    )]
                }
                if (label == null) {
                    label = "n/a"
                }

                val counts = mutableListOf<Int>()
                counts.add(1)
                if (entry.data.containsKey(context.getString(R.string.tag_count))) {
                    counts.add(entry.data[context.getString(R.string.tag_count)]!!.toIntOrNull() ?: 0)
                }
                if (entry.data.containsKey(context.getString(R.string.tag_min))) {
                    counts.add(entry.data[context.getString(R.string.tag_min)]!!.toIntOrNull() ?: 0)
                }
                if (entry.data.containsKey(context.getString(R.string.tag_max))) {
                    counts.add(entry.data[context.getString(R.string.tag_max)]!!.toIntOrNull() ?: 0)
                }
                val count = counts.maxOrNull()!!

                if (!formEntries.containsKey(label)) {
                    formEntries[label] = 0
                }

                formEntries[label] = formEntries[label]!! + count
            }
            formEntries.keys.sorted().forEach {
                reportEntries.add(MonitoringReportEntry(it, formEntries[it]!!))
            }
        }
        return reportEntries
    }
}