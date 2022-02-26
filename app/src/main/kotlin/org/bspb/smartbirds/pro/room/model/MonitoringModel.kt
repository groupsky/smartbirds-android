package org.bspb.smartbirds.pro.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bspb.smartbirds.pro.content.Monitoring

@Entity(tableName = "monitorings")
data class MonitoringModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,
    var code: String,
    var status: String = Monitoring.Status.wip.name,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: String
)