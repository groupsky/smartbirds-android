package org.bspb.smartbirds.pro.room

import androidx.room.Embedded

data class MonitoringWithEntriesCount(
    @Embedded
    val monitoring: MonitoringModel,
    val records: Int
)