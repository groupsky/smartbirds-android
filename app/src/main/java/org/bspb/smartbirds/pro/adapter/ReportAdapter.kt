package org.bspb.smartbirds.pro.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.dto.MonitoringReportEntry
import org.bspb.smartbirds.pro.enums.ReportEntryType
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.inflate

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    companion object {
        const val ITEM_TYPE_ROW = 0
        const val ITEM_TYPE_HEADER = 1
    }

    var reportEntries: List<MonitoringReportEntry>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ReportViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var labelView: TextView = itemView.findViewById(R.id.label)
        private var countView: TextView = itemView.findViewById(R.id.count)
        private var countLabelView: TextView? = itemView.findViewById(R.id.countLabel)

        fun bindItem(entry: MonitoringReportEntry) {
            labelView.text = entry.name
            countView.text = entry.count.toString()

            countLabelView?.apply {
                if (entry.count == 1) {
                    setText(R.string.record)
                } else {
                    setText(R.string.records)
                }
            }
        }
    }

    override fun getItemViewType(position: Int) = if (reportEntries != null) {
        if (reportEntries!!.lastIndex >= position && reportEntries!![position].type == ReportEntryType.header) {
            ITEM_TYPE_HEADER
        } else {
            ITEM_TYPE_ROW
        }
    } else {
        ITEM_TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val inflatedView = if (viewType == ITEM_TYPE_ROW) {
            parent.inflate(R.layout.item_report_row, false)
        } else {
            parent.inflate(R.layout.item_report_row_header, false)
        }

        return ReportViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bindItem(reportEntries?.get(position)!!)
    }

    override fun getItemCount() = if (reportEntries != null) {
        reportEntries!!.size
    } else {
        0
    }
}