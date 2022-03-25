package org.bspb.smartbirds.pro.dto

import org.bspb.smartbirds.pro.enums.ReportEntryType

data class MonitoringReportEntry(
    val name: String,
    var count: Int = 0,
    var type: ReportEntryType = ReportEntryType.row
)
