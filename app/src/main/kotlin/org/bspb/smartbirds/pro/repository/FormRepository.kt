package org.bspb.smartbirds.pro.repository

import org.bspb.smartbirds.pro.room.Form
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase

class FormRepository {

    suspend fun insertForm(form: Form) {
        SmartBirdsRoomDatabase.getInstance().formDao().insertForm(form)
    }

    suspend fun updateForm(form: Form) {
        SmartBirdsRoomDatabase.getInstance().formDao().updateForm(form)
    }

    suspend fun deleteLastEntry(monitoringCode: String) {
        SmartBirdsRoomDatabase.getInstance().formDao().deleteLastEntry(monitoringCode)
    }

    suspend fun deleteLastEntries(ids: LongArray) {
        SmartBirdsRoomDatabase.getInstance().formDao().deleteEntries(ids)
    }

    fun getEntries(code: String, entryType: String): List<Form> {
        return SmartBirdsRoomDatabase.getInstance().formDao().getEntries(code, entryType)
    }

    suspend fun findById(id: Long): Form? {
        return SmartBirdsRoomDatabase.getInstance().formDao().findById(id)
    }

}