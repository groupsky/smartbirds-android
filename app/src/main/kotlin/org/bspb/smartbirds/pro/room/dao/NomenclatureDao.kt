package org.bspb.smartbirds.pro.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.bspb.smartbirds.pro.room.NomenclatureModel

@Dao
abstract class NomenclatureDao {

    @Query("SELECT * FROM nomenclatures ORDER BY _id ASC")
    abstract suspend fun getAll(): List<NomenclatureModel>

    @Query("DELETE FROM nomenclatures")
    abstract suspend fun clearTable()

    @Insert
    abstract suspend fun insertAll(nomenclatures: List<NomenclatureModel>)

    @Transaction
    open suspend fun updateNomenclaturesAndClearOld(nomenclatures: List<NomenclatureModel>) {
        clearTable()
        insertAll(nomenclatures)
    }
}