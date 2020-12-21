package org.bspb.smartbirds.pro.ui.fragment

import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData
import java.util.*


@EFragment(R.layout.fragment_monitoring_form_common)
open class CurrentMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    @Bean
    protected lateinit var bus: EEventBus

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