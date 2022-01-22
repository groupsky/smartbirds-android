package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "forms")
data class Form(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    val code: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray
)
