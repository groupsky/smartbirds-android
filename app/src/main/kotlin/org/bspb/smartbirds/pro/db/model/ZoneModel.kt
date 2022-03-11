package org.bspb.smartbirds.pro.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zones")
data class ZoneModel(
    @PrimaryKey()
    @ColumnInfo(name = "_id")
    var id: String,
    @ColumnInfo(name = "location_id")
    var locationId: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: String?
)