package org.bspb.smartbirds.pro.ui.fragment

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.FragmentArg
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.service.DataOpsService_
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringManager


@EFragment(R.layout.fragment_monitoring_form_common)
open class BrowseMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"
    }

    private val monitoringManager = MonitoringManager.getInstance()

    @Bean
    protected lateinit var bus: EEventBus

    @FragmentArg
    protected lateinit var monitoringCode: String

    protected var monitoring: Monitoring? = null

    @AfterViews
    open fun initMonitoring() {
        lifecycleScope.launch {
            monitoring = monitoringManager.getMonitoring(monitoringCode)
            loadForm(monitoring?.commonForm)
        }
    }

    override fun persistForm(data: HashMap<String, String>) {
        monitoring?.run {
            lifecycleScope.launch {
                data[resources.getString(R.string.monitoring_id)] = code
                data[resources.getString(R.string.version)] = Configuration.STORAGE_VERSION_CODE
                commonForm.clear()
                commonForm.putAll(data)
                monitoringManager.update(this@run)
                DataOpsService_.intent(context).generateMonitoringFiles(code).start()
            }
        }
    }
}