package org.bspb.smartbirds.pro.room

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromBlob(data: ByteArray?): String? {
        data ?: return null

        // Sqlite query returns extra 00 byte in the end of blob if the data was inserted
        // with old DB implementation.
        var blobBytes = if (data.last() == 0.toByte()) {
            data.copyOf(data.size - 1)
        } else {
            data
        }
        return String(blobBytes)
    }

    @TypeConverter
    fun stringToBlob(data: String?): ByteArray? {
        return data?.toByteArray()
    }
}