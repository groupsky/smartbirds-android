package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

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
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Form

        if (id != other.id) return false
        if (code != other.code) return false
        if (type != other.type) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}