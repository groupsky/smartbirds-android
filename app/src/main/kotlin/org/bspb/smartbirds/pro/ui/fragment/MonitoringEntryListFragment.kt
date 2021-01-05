package org.bspb.smartbirds.pro.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.AbsListView
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.CursorAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import org.androidannotations.annotations.*
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.adapter.ModelCursorAdapter
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory
import org.bspb.smartbirds.pro.beans.MonitoringCursorEntries
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.service.DataOpsService
import org.bspb.smartbirds.pro.service.DataOpsService_
import org.bspb.smartbirds.pro.ui.BrowseMonitoringCommonFormActivity_
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView
import org.bspb.smartbirds.pro.ui.partial.MonitoringEntryListRowPartialView_
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringUtils.Companion.closeGpxFile
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.showAlert
import java.io.File
import java.util.*

@EFragment
@OptionsMenu(R.menu.monitoring_entry_list)
open class MonitoringEntryListFragment : ListFragment(), MonitoringCursorEntries.Listener {
    private var adapter: CursorAdapter? = null

    @Bean
    protected lateinit var entries: MonitoringCursorEntries

    @Bean
    protected lateinit var monitoringManager: MonitoringManager

    @Bean
    protected lateinit var bus: EEventBus

    @OptionsMenuItem(R.id.menu_finish_monitoring)
    protected lateinit var menuFinishMonitoring: MenuItem

    @OptionsMenuItem(R.id.menu_delete_monitoring)
    protected lateinit var menuDeleteMonitoring: MenuItem


    @Pref
    protected lateinit var globalPrefs: SmartBirdsPrefs_

    private var monitoringCode: String? = null
    private var monitoring: Monitoring? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        adapter = object : ModelCursorAdapter<MonitoringEntry?>(activity, R.layout.partial_monitoring_entry_list_row, if (entries != null) entries!!.cursor else null, ModelCursorFactory { cursor -> MonitoringManager.entryFromCursor(cursor) }) {
            override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
                return MonitoringEntryListRowPartialView_.build(context)
            }

            override fun bindView(view: View?, context: Context?, model: MonitoringEntry?) {
                require(view is MonitoringEntryListRowPartialView) { "Must use " + MonitoringEntryListRowPartialView::class.java.simpleName }
                view.bind(model)
            }
        }
        listAdapter = adapter
    }

    @AfterInject
    protected fun setupLoader() {
        monitoringCode?.let {
            monitoring = monitoringManager.getMonitoring(it)
        }
        entries.setMonitoringCode(monitoringCode)
        entries.setListener(this)
        if (adapter != null) adapter!!.swapCursor(entries.cursor)
    }

    @AfterViews
    protected fun setupListview() {
        val lv = listView
        setEmptyText(getText(R.string.emptyList))
        if (monitoring?.status == Monitoring.Status.uploaded) {
            return
        }

        lv.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        lv.setMultiChoiceModeListener(object : MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(mode: ActionMode, position: Int, id: Long, checked: Boolean) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onItemCheckedStateChanged: %d %s", position, checked))
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                Log.d(TAG, String.format(Locale.ENGLISH, "onCreateActionMode: %s", mode))
                activity?.menuInflater?.inflate(R.menu.listselection, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                Log.d(TAG, String.format(Locale.ENGLISH, "onActionItemClicked: %s", item))
                when (item.itemId) {
                    R.id.action_select_all -> {
                        var i = 0
                        while (i < lv.count) {
                            lv.setItemChecked(i, true)
                            i++
                        }
                        return true
                    }
                    R.id.action_delete -> {
                        val selectedItems = lv.checkedItemIds
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage(getString(R.string.confirm_delete_n, selectedItems.size))
                        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                            mode.finish()
                            monitoringManager.deleteEntries(selectedItems)
                            DataOpsService_.intent(activity).generateMonitoringFiles(monitoringCode).start()
                        }
                        builder.setNegativeButton(android.R.string.cancel, null)
                        val dialog = builder.create()
                        dialog.show()
                        return true
                    }
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                Log.d(TAG, String.format(Locale.ENGLISH, "onDestroyActionMode: %s", mode))
            }
        })
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val entry = listAdapter?.getItem(position) as MonitoringEntry
        if (activity is Listener) {
            (activity as Listener).onMonitoringEntrySelected(entry.id, entry.type)
        }
    }

    @FragmentArg
    open fun setMonitoringCode(monitoringCode: String?) {
        this.monitoringCode = monitoringCode
        this.monitoringCode?.let {
            if (this::entries.isInitialized) {
                entries.setMonitoringCode(it)
            }
            if (this::monitoringManager.isInitialized) {
                monitoring = monitoringManager.getMonitoring(it)
            }
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        monitoring?.apply {
            menuFinishMonitoring.isVisible = status == Monitoring.Status.paused
        }
    }

    override fun onMonitoringEntriesChanged(entries: MonitoringCursorEntries) {
        adapter?.run {
            swapCursor(entries.cursor)
        }
    }

    @OptionsItem(R.id.menu_common_form)
    open fun onCommonForm() {
        BrowseMonitoringCommonFormActivity_.intent(this).monitoringCode(monitoringCode).start()
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
                context?.let { closeGpxFile(it, this) }
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
            debugLog("Exists: " + dir.exists())
            dir.deleteRecursively();
            debugLog("Exists after delete: " + dir.exists())
            activity?.finish();
        }
    }

    interface Listener {
        fun onMonitoringEntrySelected(id: Long, entryType: EntryType?)
    }

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".FFormLst"
    }
}