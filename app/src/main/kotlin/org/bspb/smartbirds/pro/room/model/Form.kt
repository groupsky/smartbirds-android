package org.bspb.smartbirds.pro.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forms")
data class Form(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,
    val code: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: String
)