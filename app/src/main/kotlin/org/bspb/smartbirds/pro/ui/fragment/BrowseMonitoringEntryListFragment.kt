package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs
import org.bspb.smartbirds.pro.ui.BrowseMonitoringCommonFormActivity
import org.bspb.smartbirds.pro.ui.MonitoringReportActivity
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringUtils
import org.bspb.smartbirds.pro.utils.showAlert
import java.util.Date

class BrowseMonitoringEntryListFragment : MonitoringEntryListFragment() {

    companion object {
        fun newInstance(monitoringCode: String?): BrowseMonitoringEntryListFragment {
            return BrowseMonitoringEntryListFragment().apply {
                arguments = Bundle().apply {
                    monitoringCode?.let {
                        putString(ARG_MONITORING_CODE, monitoringCode)
                    }
                }
            }
        }
    }

    private lateinit var menuFinishMonitoring: MenuItem
    private lateinit var menuDeleteMonitoring: MenuItem

    private lateinit var globalPrefs: SmartBirdsPrefs

    private val bus: EEventBus by lazy { EEventBus.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        globalPrefs = SmartBirdsPrefs(requireContext())
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.monitoring_entry_list, menu)
        menuFinishMonitoring = menu.findItem(R.id.menu_finish_monitoring)
        menuDeleteMonitoring = menu.findItem(R.id.menu_delete_monitoring)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.menu_common_form) {
            onCommonForm()
            return true
        }
        if (itemId == R.id.menu_finish_monitoring) {
            onFinishMonitoring()
            return true
        }
        if (itemId == R.id.menu_delete_monitoring) {
            onDeleteMonitoring()
            return true
        }
        if (itemId == R.id.menu_report) {
            showMonitoringReport()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        monitoring?.apply {
            menuFinishMonitoring.isVisible =
                status == Monitoring.Status.paused || status == Monitoring.Status.canceled
        }
    }

    private fun onCommonForm() {
        context?.let {
            startActivity(BrowseMonitoringCommonFormActivity.newIntent(it, code))
        }

    }

    private fun onFinishMonitoring() {
        context?.showAlert(
            R.string.finish_monitoring_confirm_title,
            R.string.finish_monitoring_confirm_message,
            { _, _ ->
                finishMonitoring()
            },
            null
        )

    }

    private fun onDeleteMonitoring() {
        context?.showAlert(
            R.string.delete_monitoring_confirm_title,
            R.string.delete_monitoring_confirm_message,
            { _, _ ->
                deleteMonitoring()
            },
            null
        )

    }

    private fun showMonitoringReport() {
        if (code != null) {
            startActivity(MonitoringReportActivity.newIntent(requireContext(), code!!))
        } else {
            Toast.makeText(requireContext(), "Invalid monitoring code", Toast.LENGTH_LONG).show()
        }
    }

    private fun finishMonitoring() {
        monitoring?.apply {
            lifecycleScope.launch {
                val pausedMonitoring = monitoringManager.getPausedMonitoring()

                // Apply monitoring code if missing in common form
                if (!commonForm.containsKey(resources.getString(R.string.monitoring_id))) {
                    commonForm[resources.getString(R.string.monitoring_id)] = code
                    monitoringManager.update(this@apply)
                }

                if (commonForm.containsKey(resources.getString(R.string.end_time_key))) {
                    if (TextUtils.isEmpty(commonForm[resources.getString(R.string.end_time_key)])) {
                        commonForm[resources.getString(R.string.end_time_key)] =
                            Configuration.STORAGE_TIME_FORMAT.format(Date())
                        monitoringManager.update(this@apply)
                    }
                }

                monitoringManager.updateStatus(this@apply, Monitoring.Status.finished)

                if (code == pausedMonitoring?.code) {
                    context?.let { MonitoringUtils.closeGpxFile(it, this@apply) }
                    globalPrefs.setRunningMonitoring(false)
                    globalPrefs.setPausedMonitoring(false)
                }
                bus.postSticky(MonitoringFinishedEvent())
                activity?.finish()
            }
        }

    }

    private fun deleteMonitoring() {
        monitoring?.apply {
            lifecycleScope.launch {
                monitoringManager.deleteMonitoring(code)
                val dir = MonitoringUtils.getMonitoringDir(requireContext(), code);
                dir.deleteRecursively();
                activity?.finish();
            }

        }
    }
}