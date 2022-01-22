package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "zones")
data class Zone(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    @ColumnInfo(name = "location_id")
    val locationId: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray?
)
