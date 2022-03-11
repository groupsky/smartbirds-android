package org.bspb.smartbirds.pro.db.model

import androidx.room.Embedded

data class MonitoringWithEntriesCount(
    @Embedded
    val monitoring: MonitoringModel,
    val records: Int
)