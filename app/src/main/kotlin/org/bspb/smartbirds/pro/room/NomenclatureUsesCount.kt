package org.bspb.smartbirds.pro.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName = "nomenclature_uses_count")
data class NomenclatureUsesCount(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,
    var type: String?,
    @ColumnInfo(name = "label_id")
    var labelId: String?,
    var count: Int?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var data: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NomenclatureUsesCount

        if (id != other.id) return false
        if (type != other.type) return false
        if (labelId != other.labelId) return false
        if (count != other.count) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (labelId?.hashCode() ?: 0)
        result = 31 * result + (count ?: 0)
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
