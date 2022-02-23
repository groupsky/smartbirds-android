package org.bspb.smartbirds.pro.room

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
    var data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonitoringModel

        if (id != other.id) return false
        if (code != other.code) return false
        if (status != other.status) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}