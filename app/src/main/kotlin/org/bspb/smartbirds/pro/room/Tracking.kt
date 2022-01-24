package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
data class Tracking(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    val code: String,
    val time: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?
)
