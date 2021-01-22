package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CursorAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.adapter.ModelCursorAdapter
import org.bspb.smartbirds.pro.adapter.ModelCursorFactory
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.db.MonitoringColumns
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Monitorings
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView
import org.bspb.smartbirds.pro.ui.partial.MonitoringListRowPartialView_

@EFragment
@OptionsMenu(R.menu.monitoring_list)
open class MonitoringListFragment : ListFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private val PROJECTION = arrayOf(
                MonitoringColumns._ID,
                MonitoringColumns.CODE,
                MonitoringColumns.STATUS,
                MonitoringColumns.DATA,
                MonitoringColumns.ENTRIES_COUNT)

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

    private var adapter: CursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            getString(KEY_STATUS)?.apply {
                filterStatus = Monitoring.Status.valueOf(this)
            }
        }

        adapter = object : ModelCursorAdapter<Monitoring?>(activity, R.layout.partial_monitoring_list_row, null, ModelCursorFactory { cursor -> MonitoringManager.monitoringFromCursor(cursor) }) {
            override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
                return MonitoringListRowPartialView_.build(context)
            }

            override fun bindView(view: View?, context: Context?, model: Monitoring?) {
                require(view is MonitoringListRowPartialView) { "Must use " + MonitoringListRowPartialView::class.java.simpleName }
                view.bind(model)
            }
        }
        listAdapter = adapter
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        LoaderManager.getInstance(this).restartLoader(0, null, this)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val monitoring = listAdapter?.getItem(position) as Monitoring
        if (monitoring != null && activity is Listener) {
            (activity as Listener).onMonitoringSelected(monitoring.code)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader")
        var selection: String? = "(" + MonitoringColumns.ENTRIES_COUNT + "> 0 OR " + MonitoringColumns.STATUS + "<> 'canceled') "
        var selectionArgs: Array<String>? = null

        filterStatus?.apply {
            selection += " AND " + MonitoringColumns.STATUS + "=?"
            selectionArgs = arrayOf(this.name)
        }
        return CursorLoader(requireContext(),
                Monitorings.CONTENT_URI,
                PROJECTION,
                selection,
                selectionArgs,
                MonitoringColumns._ID + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        Log.d(TAG, String.format("onLoadFinished: %d", cursor?.count))
        adapter!!.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset")
        adapter!!.swapCursor(null)
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
        LoaderManager.getInstance(this).restartLoader(0, null, this)
        activity?.title = if (filterStatus != null) {
            getString(R.string.title_monitoring_list) + " [" + getString(filterStatus!!.label) + "]"
        } else {
            getString(R.string.title_monitoring_list)
        }

    }

    private fun updateMenuItems(status: Monitoring.Status?) {
        menuStatusFinished.setChecked(false)
        menuStatusUploaded.setChecked(false)
        menuStatusPaused.setChecked(false)
        menuStatusCanceled.setChecked(false)

        status?.apply {
            when (this) {
                Monitoring.Status.finished ->
                    menuStatusFinished.setChecked(true)
                Monitoring.Status.uploaded ->
                    menuStatusUploaded.setChecked(true)
                Monitoring.Status.paused ->
                    menuStatusPaused.setChecked(true)
                Monitoring.Status.canceled ->
                    menuStatusCanceled.setChecked(true)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_STATUS, filterStatus?.name)
    }
}