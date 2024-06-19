package org.bspb.smartbirds.pro.ui.fragment

import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData


@EFragment(R.layout.fragment_monitoring_form_common)
open class CurrentMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    protected val bus: EEventBus by lazy { EEventBus.getInstance() }

    @FragmentArg
    @JvmField
    protected var isFinishing: Boolean = false

    override fun onStart() {
        super.onStart()
        bus.register(this)
        bus.postSticky(GetMonitoringCommonData())
    }

    override fun onStop() {
        bus.unregister(this)
        super.onStop()
    }


    override fun persistForm(data: HashMap<String, String>) {
        bus.post(SetMonitoringCommonData(data))
    }

    fun onEventMainThread(event: MonitoringCommonData) {
        loadForm(event.data)
    }
}