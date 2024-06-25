package org.bspb.smartbirds.pro.ui.fragment

import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.androidannotations.annotations.EFragment
import org.androidannotations.annotations.OptionsItem
import org.androidannotations.annotations.OptionsMenu
import org.androidannotations.annotations.OptionsMenuItem
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.ui.BrowseMonitoringCommonFormActivity
import org.bspb.smartbirds.pro.ui.MonitoringReportActivity
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringUtils
import org.bspb.smartbirds.pro.utils.showAlert
import java.util.Date

@EFragment
@OptionsMenu(R.menu.monitoring_entry_list)
open class BrowseMonitoringEntryListFragment : MonitoringEntryListFragment() {

    @OptionsMenuItem(R.id.menu_finish_monitoring)
    protected lateinit var menuFinishMonitoring: MenuItem

    @OptionsMenuItem(R.id.menu_delete_monitoring)
    protected lateinit var menuDeleteMonitoring: MenuItem

    @Pref
    protected lateinit var globalPrefs: SmartBirdsPrefs_

    protected val bus: EEventBus by lazy { EEventBus.getInstance() }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        monitoring?.apply {
            menuFinishMonitoring.isVisible =
                status == Monitoring.Status.paused || status == Monitoring.Status.canceled
        }
    }

    @OptionsItem(R.id.menu_common_form)
    open fun onCommonForm() {
        context?.let {
            startActivity(BrowseMonitoringCommonFormActivity.newIntent(it, code))
        }

    }

    @OptionsItem(R.id.menu_finish_monitoring)
    open fun onFinishMonitoring() {
        context?.showAlert(
            R.string.finish_monitoring_confirm_title,
            R.string.finish_monitoring_confirm_message,
            { _, _ ->
                finishMonitoring()
            },
            null
        )

    }

    @OptionsItem(R.id.menu_delete_monitoring)
    open fun onDeleteMonitoring() {
        context?.showAlert(
            R.string.delete_monitoring_confirm_title,
            R.string.delete_monitoring_confirm_message,
            { _, _ ->
                deleteMonitoring()
            },
            null
        )

    }

    @OptionsItem(R.id.menu_report)
    protected open fun showMonitoringReport() {
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
                    globalPrefs.runningMonitoring().put(false)
                    globalPrefs.pausedMonitoring().put(false)
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