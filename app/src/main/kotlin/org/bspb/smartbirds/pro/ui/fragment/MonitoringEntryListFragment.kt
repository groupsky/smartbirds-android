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
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.adapter.MonitoringEntryListAdapter
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.viewmodel.MonitoringEntryListViewModel
import java.util.Locale

open class MonitoringEntryListFragment : ListFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".FFormLst"

        const val ARG_MONITORING_CODE = "monitoringCode"

        fun newInstance(monitoringCode: String?): MonitoringEntryListFragment {
            return MonitoringEntryListFragment().apply {
                arguments = Bundle().apply {
                    monitoringCode?.let {
                        putString(ARG_MONITORING_CODE, monitoringCode)
                    }
                }
            }
        }
    }

    private var adapter: MonitoringEntryListAdapter? = null

    protected val monitoringManager = MonitoringManager.getInstance()

    protected var code: String? = null
    protected var monitoring: Monitoring? = null

    private val viewModel: MonitoringEntryListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        readArgs()
        setupLoader()
        super.onCreate(savedInstanceState)

        adapter = MonitoringEntryListAdapter(requireContext())
        listAdapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListview()
    }

    private fun readArgs() {
        setMonitoringCode(arguments?.getString(ARG_MONITORING_CODE))
    }

    private fun initViewModel() {
        viewModel.init(code)
        viewModel.entries?.observe(viewLifecycleOwner) {
            adapter?.apply {
                clear()
                addAll(it)
                notifyDataSetChanged()
            }
        }
    }

    private fun setupLoader() {
        code?.let {
            lifecycleScope.launch {
                monitoring = monitoringManager.getMonitoring(it)
            }
        }
    }

    private fun setupListview() {
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
                                monitoringManager.deleteEntries(selectedItems)
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

    fun setMonitoringCode(monitoringCode: String?) {
        this.code = monitoringCode
        this.code?.let {
            lifecycleScope.launch {
                monitoring = monitoringManager.getMonitoring(it)
            }
        }

    }

    interface Listener {
        fun onMonitoringEntrySelected(id: Long, entryType: EntryType?)
    }
}