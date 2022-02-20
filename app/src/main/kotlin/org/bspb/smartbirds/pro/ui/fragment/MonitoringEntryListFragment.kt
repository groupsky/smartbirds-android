package org.bspb.smartbirds.pro.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.adapter.MonitoringEntryListAdapter
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.service.DataOpsService_
import org.bspb.smartbirds.pro.utils.MonitoringManagerNew
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.viewmodel.MonitoringEntryListViewModel
import java.util.*

@EFragment
open class MonitoringEntryListFragment : ListFragment() {
    private var adapter: MonitoringEntryListAdapter? = null

    @Bean
    protected lateinit var monitoringManager: MonitoringManager
    protected val monitoringManagerNew = MonitoringManagerNew.getInstance()

    protected var code: String? = null
    protected var monitoring: Monitoring? = null

    private val viewModel: MonitoringEntryListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        adapter = MonitoringEntryListAdapter(requireContext())
        listAdapter = adapter
    }

    private fun initViewModel() {
        debugLog("init viewModel: $code")
        viewModel.init(code)
        viewModel.entries?.observe(viewLifecycleOwner) {
            debugLog("Entries found: $it")
            adapter?.apply {
                clear()
                addAll(it)
                notifyDataSetChanged()
            }
        }
    }

    @AfterInject
    protected fun setupLoader() {
        code?.let {
            monitoring = monitoringManager.getMonitoring(it)
        }
    }

    @AfterViews
    protected fun setupListview() {
        initViewModel()
        val lv = listView
        setEmptyText(getText(R.string.emptyList))
        if (monitoring?.status == Monitoring.Status.uploaded) {
            return
        }

        lv.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
        lv.setMultiChoiceModeListener(object : MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(
                mode: ActionMode,
                position: Int,
                id: Long,
                checked: Boolean
            ) {
                Log.d(
                    TAG,
                    String.format(
                        Locale.ENGLISH,
                        "onItemCheckedStateChanged: %d %s",
                        position,
                        checked
                    )
                )
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
                        builder.setPositiveButton(android.R.string.ok) { _, _ ->
                            lifecycleScope.launch {
                                mode.finish()
                                monitoringManagerNew.deleteEntries(selectedItems)
                                DataOpsService_.intent(activity).generateMonitoringFiles(code).start()
                            }
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
        this.code = monitoringCode
        this.code?.let {
            if (viewModel.entries == null) {
                initViewModel()
            }
            if (this::monitoringManager.isInitialized) {
                monitoring = monitoringManager.getMonitoring(it)
            }
        }

    }

    interface Listener {
        fun onMonitoringEntrySelected(id: Long, entryType: EntryType?)
    }

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".FFormLst"
    }
}