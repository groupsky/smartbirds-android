package org.bspb.smartbirds.pro.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView_

class MonitoringListAdapter(context: Context) :
    ArrayAdapter<Monitoring>(context, R.layout.partial_monitoring_list_row) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var resultView = convertView
        if (resultView == null) {
            resultView = MonitoringListRowPartialView_.build(context)
        }
        require(resultView is MonitoringListRowPartialView) { "Must use " + MonitoringListRowPartialView::class.java.simpleName }
        resultView.bind(getItem(position))
        return resultView;
    }

}