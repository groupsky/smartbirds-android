package org.bspb.smartbirds.pro.ui.fragment

import android.view.View
import android.view.View.*
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.androidannotations.annotations.ViewById
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import java.util.*


@EFragment(R.layout.fragment_monitoring_form_common)
open class CurrentMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    @Bean
    protected lateinit var bus: EEventBus

    @FragmentArg
    @JvmField
    protected var isFinishing: Boolean = false

    @ViewById(R.id.form_common_methodology)
    protected lateinit var observationMethodology: SingleChoiceFormInput

    @ViewById(R.id.form_common_methodology_container)
    protected lateinit var observationMethodologyContainer: View

    override fun onStart() {
        super.onStart()
        bus.register(this)
        bus.postSticky(GetMonitoringCommonData())
        observationMethodologyContainer.visibility = if (isFinishing) VISIBLE else GONE
        observationMethodology.isRequired = isFinishing
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