package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "nomenclature_uses_count")
data class NomenclatureUsesCount(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0,
    var type: String?,
    @ColumnInfo(name = "label_id")
    var labelId: String?,
    var count: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray?
)
