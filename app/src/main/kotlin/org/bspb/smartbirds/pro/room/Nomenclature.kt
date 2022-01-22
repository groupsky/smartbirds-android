package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "nomenclatures")
data class Nomenclature(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    val type: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray?
)
