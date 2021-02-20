package org.bspb.smartbirds.pro.ui.partial

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EViewGroup
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.res.StringRes
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring

@EViewGroup(R.layout.partial_monitoring_list_row)
open class MonitoringListRowPartialView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".MonLRPV"
    }

    @ViewById(R.id.place)
    protected lateinit var placeView: TextView

    @ViewById(R.id.start)
    protected lateinit var startView: TextView

    @ViewById(R.id.status)
    protected lateinit var statusView: TextView

    @ViewById(R.id.entries)
    protected lateinit var entriesView: TextView

    @StringRes(R.string.tag_location)
    protected lateinit var tagLocation: String

    @StringRes(R.string.tag_begin_date)
    protected lateinit var tagBeginDate: String

    @StringRes(R.string.tag_begin_time)
    protected lateinit var tagBeginTime: String

    private var monitoring: Monitoring? = null

    @AfterViews
    protected fun bind() {
        Log.d(TAG, "bind after views")
        if (monitoring != null) bind(monitoring)
    }

    fun bind(monitoring: Monitoring?) {
        Log.d(TAG, "bind")
        this.monitoring = monitoring
        if (monitoring == null) return
        monitoring.commonForm
        placeView.text = monitoring.commonForm[tagLocation]
        startView.text = monitoring.commonForm[tagBeginDate].toString() + ", " + monitoring.commonForm[tagBeginTime]
        statusView.text = resources.getString(monitoring.status.label)
        entriesView.text = "" + monitoring.entriesCount

    }

}