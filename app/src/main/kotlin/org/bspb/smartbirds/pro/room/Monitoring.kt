package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "monitorings")
data class Monitoring(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    val code: String,
    val status: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val data: ByteArray
)
