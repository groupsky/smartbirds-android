package org.bspb.smartbirds.pro.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.bspb.smartbirds.pro.db.model.Form
import org.jetbrains.annotations.TestOnly

@Dao
interface FormDao {

    @Insert
    suspend fun insertForm(form: Form)

    @Update
    suspend fun updateForm(form: Form)

    @Query("DELETE FROM forms WHERE  _id = (SELECT _id from forms WHERE code = :monitoringCode ORDER BY _id DESC LIMIT 1)")
    suspend fun deleteLastEntry(monitoringCode: String): Int

    @Query("DELETE FROM forms WHERE _id in (:ids)")
    suspend fun deleteEntries(ids: LongArray)

    @Query("DELETE FROM forms WHERE code = :monitoringCode")
    suspend fun deleteByMonitoringCode(monitoringCode: String)

    @Query("SELECT * FROM forms WHERE code = :code AND (type = :type OR :type IS NULL)")
    fun getEntries(code: String, type: String?): List<Form>

    @Query("SELECT * FROM forms WHERE _id = :id")
    suspend fun findById(id: Long): Form?

    @Query("SELECT * FROM forms WHERE code = :code")
    fun findAllByMonitoringCode(code: String): LiveData<List<Form>>

    @TestOnly
    @Query("SELECT * FROM forms")
    fun findAll(): List<Form>

}