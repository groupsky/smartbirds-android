package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "zones")
data class ZoneModel(
    @PrimaryKey()
    @ColumnInfo(name = "_id")
    var id: String,
    @ColumnInfo(name = "location_id")
    var locationId: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZoneModel

        if (id != other.id) return false
        if (locationId != other.locationId) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (locationId ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}