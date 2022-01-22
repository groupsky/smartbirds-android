package org.bspb.smartbirds.pro.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.bspb.smartbirds.pro.room.Nomenclature

@Dao
abstract class NomenclatureDao {

    @Query("DELETE FROM nomenclatures")
    abstract fun clearTable()

    @Insert
    abstract fun insertAll(nomenclatures: List<Nomenclature>)

    @Transaction
    open fun updateNomenclaturesAndClearOld(nomenclatures: List<Nomenclature>) {
        clearTable()
        insertAll(nomenclatures)
    }
}