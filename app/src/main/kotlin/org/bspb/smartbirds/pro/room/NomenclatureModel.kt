package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "nomenclatures")
class NomenclatureModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0,
    val type: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray?,
)
