package org.bspb.smartbirds.pro.ui.fragment

import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import org.androidannotations.annotations.*
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.service.DataOpsService
import org.bspb.smartbirds.pro.service.DataOpsService_
import org.bspb.smartbirds.pro.ui.BrowseMonitoringCommonFormActivity_
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringUtils
import org.bspb.smartbirds.pro.utils.showAlert
import java.util.*

@EFragment
@OptionsMenu(R.menu.monitoring_entry_list)
open class BrowseMonitoringEntryListFragment : MonitoringEntryListFragment() {

    @OptionsMenuItem(R.id.menu_finish_monitoring)
    protected lateinit var menuFinishMonitoring: MenuItem

    @OptionsMenuItem(R.id.menu_delete_monitoring)
    protected lateinit var menuDeleteMonitoring: MenuItem

    @Pref
    protected lateinit var globalPrefs: SmartBirdsPrefs_

    @Bean
    protected lateinit var bus: EEventBus

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        monitoring?.apply {
            menuFinishMonitoring.isVisible = status == Monitoring.Status.paused
        }
    }

    @OptionsItem(R.id.menu_common_form)
    open fun onCommonForm() {
        BrowseMonitoringCommonFormActivity_.intent(this).monitoringCode(code).start()
    }

    @OptionsItem(R.id.menu_finish_monitoring)
    open fun onFinishMonitoring() {
        context?.showAlert(R.string.finish_monitoring_confirm_title, R.string.finish_monitoring_confirm_message, { _, _ ->
            finishMonitoring()
        }, null)

    }

    @OptionsItem(R.id.menu_delete_monitoring)
    open fun onDeleteMonitoring() {
        context?.showAlert(R.string.delete_monitoring_confirm_title, R.string.delete_monitoring_confirm_message, { _, _ ->
            deleteMonitoring()
        }, null)

    }

    private fun finishMonitoring() {
        monitoring?.apply {
            val pausedMonitoring = monitoringManager.pausedMonitoring

            if (commonForm.containsKey(resources.getString(R.string.end_time_key))) {
                if (TextUtils.isEmpty(commonForm[resources.getString(R.string.end_time_key)])) {
                    commonForm[resources.getString(R.string.end_time_key)] = Configuration.STORAGE_TIME_FORMAT.format(Date())
                    monitoringManager.update(this)
                }
            }

            monitoringManager.updateStatus(this, Monitoring.Status.finished)

            if (code == pausedMonitoring?.code) {
                context?.let { MonitoringUtils.closeGpxFile(it, this) }
                globalPrefs.runningMonitoring().put(false)
                globalPrefs.pausedMonitoring().put(false)
            }
            DataOpsService_.intent(context).generateMonitoringFiles(code).start()
            bus.postSticky(MonitoringFinishedEvent())
        }

    }

    private fun deleteMonitoring() {
        monitoring?.apply {
            monitoringManager.deleteMonitoring(code)
            var dir = DataOpsService.getMonitoringDir(context, code);
            dir.deleteRecursively();
            activity?.finish();
        }
    }
}