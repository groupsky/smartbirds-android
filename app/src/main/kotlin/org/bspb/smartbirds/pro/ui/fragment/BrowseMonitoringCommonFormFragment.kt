package org.bspb.smartbirds.pro.ui.fragment

import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.ui.utils.Configuration
import java.util.*


@EFragment(R.layout.fragment_monitoring_form_common)
open class BrowseMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    @Bean
    protected lateinit var monitoringManager: MonitoringManager

    @Bean
    protected lateinit var bus: EEventBus

    @FragmentArg
    protected lateinit var monitoringCode: String

    protected var monitoring: Monitoring? = null

    @AfterViews
    open fun initMonitoring() {
        monitoring = monitoringManager.getMonitoring(monitoringCode)
        loadForm(monitoring?.commonForm)
    }

    override fun persistForm(data: HashMap<String, String>) {
        monitoring?.run {
            data[resources.getString(R.string.monitoring_id)] = code
            data[resources.getString(R.string.version)] = Configuration.STORAGE_VERSION_CODE
            commonForm.clear()
            commonForm.putAll(data)
            monitoringManager.update(this)
        }
    }
}