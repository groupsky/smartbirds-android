package org.bspb.smartbirds.pro.room.model

import androidx.room.Embedded

data class MonitoringWithEntriesCount(
    @Embedded
    val monitoring: MonitoringModel,
    val records: Int
)