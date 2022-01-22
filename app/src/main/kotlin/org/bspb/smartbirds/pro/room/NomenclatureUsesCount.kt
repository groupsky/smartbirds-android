package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "nomenclature_uses_count")
data class NomenclatureUsesCount(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    val type: String?,
    @ColumnInfo(name = "label_id")
    val labelId: String?,
    val count: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray?
)
