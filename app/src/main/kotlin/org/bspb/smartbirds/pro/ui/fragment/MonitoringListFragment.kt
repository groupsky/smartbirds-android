package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.fragment.app.viewModels
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.adapter.MonitoringListAdapter
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.viewmodel.MonitoringListViewModel

@EFragment
@OptionsMenu(R.menu.monitoring_list)
open class MonitoringListFragment : ListFragment() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".FMonLst"
        private const val KEY_STATUS = "status"
    }

    private var filterStatus: Monitoring.Status? = null

    @OptionsMenuItem(R.id.menu_filter_status_finished)
    protected lateinit var menuStatusFinished: MenuItem

    @OptionsMenuItem(R.id.menu_filter_status_uploaded)
    protected lateinit var menuStatusUploaded: MenuItem

    @OptionsMenuItem(R.id.menu_filter_status_canceled)
    protected lateinit var menuStatusCanceled: MenuItem

    @OptionsMenuItem(R.id.menu_filter_status_paused)
    protected lateinit var menuStatusPaused: MenuItem

    private var adapter: MonitoringListAdapter? = null

    private val viewModel: MonitoringListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            getString(KEY_STATUS)?.apply {
                filterStatus = Monitoring.Status.valueOf(this)
            }
        }

        adapter = MonitoringListAdapter(requireContext())
        listAdapter = adapter
    }

    @AfterViews
    protected open fun initViewModel() {
        viewModel.init()
        viewModel.entries?.observe(viewLifecycleOwner) { items ->
            items?.apply {
                adapter?.clear()
                adapter?.addAll(items)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val monitoring = listAdapter?.getItem(position) as Monitoring
        if (monitoring != null && activity is Listener) {
            (activity as Listener).onMonitoringSelected(monitoring.code)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        filterStatus?.apply {
            updateMenuItems(this)
        }
    }

    interface Listener {
        fun onMonitoringSelected(monitoringCode: String?)
    }

    @AfterViews
    protected open fun setupListView() {
        setEmptyText(getText(R.string.emptyList))
    }

    @OptionsItem(R.id.menu_filter_status_finished)
    protected open fun filterFinished() {
        filterByStatus(Monitoring.Status.finished)
    }

    @OptionsItem(R.id.menu_filter_status_uploaded)
    protected open fun filterUploaded() {
        filterByStatus(Monitoring.Status.uploaded)
    }

    @OptionsItem(R.id.menu_filter_status_paused)
    protected open fun filterPaused() {
        filterByStatus(Monitoring.Status.paused)
    }

    @OptionsItem(R.id.menu_filter_status_canceled)
    protected open fun filterCanceled() {
        filterByStatus(Monitoring.Status.canceled)
    }

    private fun filterByStatus(status: Monitoring.Status) {
        filterStatus = if (filterStatus == status) {
            null
        } else {
            status
        }

        updateMenuItems(filterStatus)
        viewModel.setFilterStatus(filterStatus)
        activity?.title = if (filterStatus != null) {
            getString(R.string.title_monitoring_list) + " [" + getString(filterStatus!!.label) + "]"
        } else {
            getString(R.string.title_monitoring_list)
        }

    }

    private fun updateMenuItems(status: Monitoring.Status?) {
        menuStatusFinished.isChecked = false
        menuStatusUploaded.isChecked = false
        menuStatusPaused.isChecked = false
        menuStatusCanceled.isChecked = false

        status?.apply {
            when (this) {
                Monitoring.Status.finished ->
                    menuStatusFinished.isChecked = true
                Monitoring.Status.uploaded ->
                    menuStatusUploaded.isChecked = true
                Monitoring.Status.paused ->
                    menuStatusPaused.isChecked = true
                Monitoring.Status.canceled ->
                    menuStatusCanceled.isChecked = true
                else -> {}
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_STATUS, filterStatus?.name)
    }
}