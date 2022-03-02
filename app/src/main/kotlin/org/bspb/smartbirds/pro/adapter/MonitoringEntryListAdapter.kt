package org.bspb.smartbirds.pro.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_

class MonitoringEntryListAdapter(context: Context) :
    ArrayAdapter<MonitoringEntry>(context, R.layout.partial_monitoring_entry_list_row) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var resultView = convertView
        if (resultView == null) {
            resultView = MonitoringEntryListRowPartialView_.build(context)
        }
        require(resultView is MonitoringEntryListRowPartialView) { "Must use " + MonitoringEntryListRowPartialView::class.java.simpleName }
        resultView.bind(getItem(position))
        return resultView;
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

}