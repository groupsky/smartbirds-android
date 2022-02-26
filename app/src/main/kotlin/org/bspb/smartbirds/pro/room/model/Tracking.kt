package org.bspb.smartbirds.pro.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking")
data class Tracking(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,
    var code: String?,
    var time: Long?,
    var latitude: Double?,
    var longitude: Double?,
    var altitude: Double?
)
