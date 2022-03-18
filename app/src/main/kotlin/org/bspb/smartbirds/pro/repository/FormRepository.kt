package org.bspb.smartbirds.pro.repository

import androidx.lifecycle.LiveData
import org.bspb.smartbirds.pro.db.model.Form
import org.bspb.smartbirds.pro.db.SmartBirdsDatabase

class FormRepository {

    suspend fun insertForm(form: Form) {
        SmartBirdsDatabase.getInstance().formDao().insertForm(form)
    }

    suspend fun updateForm(form: Form) {
        SmartBirdsDatabase.getInstance().formDao().updateForm(form)
    }

    suspend fun deleteLastEntry(monitoringCode: String) {
        SmartBirdsDatabase.getInstance().formDao().deleteLastEntry(monitoringCode)
    }

    suspend fun deleteLastEntries(ids: LongArray) {
        SmartBirdsDatabase.getInstance().formDao().deleteEntries(ids)
    }

    suspend fun deleteMonitoringEntries(monitoringCode: String) {
        SmartBirdsDatabase.getInstance().formDao().deleteByMonitoringCode(monitoringCode)
    }

    fun getEntries(code: String, entryType: String?): List<Form> {
        return SmartBirdsDatabase.getInstance().formDao().getEntries(code, entryType)
    }

    suspend fun findById(id: Long): Form? {
        return SmartBirdsDatabase.getInstance().formDao().findById(id)
    }

    fun findAllByMonitoringCode(code: String): LiveData<List<Form>> {
        return SmartBirdsDatabase.getInstance().formDao().findAllByMonitoringCode(code)
    }

}