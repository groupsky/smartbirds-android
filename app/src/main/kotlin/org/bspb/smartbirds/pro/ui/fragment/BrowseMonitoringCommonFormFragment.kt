package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringManager


class BrowseMonitoringCommonFormFragment : BaseCommonFormFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".CommonForm"

        private const val ARG_MONITORING_CODE = "monitoringCode"

        fun newInstance(monitoringCode: String): BrowseMonitoringCommonFormFragment {
            return BrowseMonitoringCommonFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MONITORING_CODE, monitoringCode)
                }
            }
        }
    }

    private val monitoringManager = MonitoringManager.getInstance()
    private lateinit var monitoringCode: String
    private var monitoring: Monitoring? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let { args ->
            monitoringCode = args.getString(ARG_MONITORING_CODE) ?: ""
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_monitoring_form_common,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMonitoring()
    }

    private fun initMonitoring() {
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
            }
        }
    }
}