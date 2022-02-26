package org.bspb.smartbirds.pro.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nomenclatures")
data class NomenclatureModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,
    var type: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: String?,
)