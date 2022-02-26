package org.bspb.smartbirds.pro.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.bspb.smartbirds.pro.db.model.NomenclatureUsesCount

@Dao
abstract class NomenclatureUsesCountDao {
    @Query("SELECT * FROM nomenclature_uses_count WHERE type = :type ORDER BY count DESC")
    abstract suspend fun findByType(type: String): List<NomenclatureUsesCount>

    @Query("SELECT * FROM nomenclature_uses_count WHERE label_id = :label ORDER BY _id ASC")
    abstract suspend fun findByLabel(label: String): NomenclatureUsesCount?

    @Insert
    abstract suspend fun insert(nomenclatureUsesCount: NomenclatureUsesCount)

    @Update
    abstract suspend fun update(nomenclatureUsesCount: NomenclatureUsesCount)
}