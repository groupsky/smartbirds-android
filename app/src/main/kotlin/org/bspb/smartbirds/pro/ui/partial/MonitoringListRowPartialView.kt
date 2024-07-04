package org.bspb.smartbirds.pro.ui.partial

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring

open class MonitoringListRowPartialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".MonLRPV"

        fun build(context: Context): MonitoringListRowPartialView {
            val instance = MonitoringListRowPartialView(context)
            instance.onFinishInflate()
            return instance
        }
    }

    private var alreadyInflated: Boolean = false

    private lateinit var placeView: TextView
    private lateinit var startView: TextView
    private lateinit var statusView: TextView
    private lateinit var entriesView: TextView
    private lateinit var tagLocation: String
    private lateinit var tagBeginDate: String
    private lateinit var tagBeginTime: String

    private var monitoring: Monitoring? = null

    override fun onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true
            inflate(context, R.layout.partial_monitoring_list_row, this)
            bind()
        }
        super.onFinishInflate()
    }

    protected fun bind() {
        Log.d(TAG, "bind after views")
        placeView = findViewById(R.id.place)
        startView = findViewById(R.id.start)
        statusView = findViewById(R.id.status)
        entriesView = findViewById(R.id.entries)

        tagLocation = resources.getString(R.string.tag_location)
        tagBeginDate = resources.getString(R.string.tag_begin_date)
        tagBeginTime = resources.getString(R.string.tag_begin_time)

        if (monitoring != null) bind(monitoring)
    }

    fun bind(monitoring: Monitoring?) {
        Log.d(TAG, "bind")
        this.monitoring = monitoring
        if (monitoring == null) return
        monitoring.commonForm
        placeView.text = monitoring.commonForm[tagLocation]
        startView.text =
            monitoring.commonForm[tagBeginDate].toString() + ", " + monitoring.commonForm[tagBeginTime]
        statusView.text = resources.getString(monitoring.status.label)
        entriesView.text = "" + monitoring.entriesCount

    }

}