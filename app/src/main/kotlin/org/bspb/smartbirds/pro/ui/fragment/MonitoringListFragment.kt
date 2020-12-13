package org.bspb.smartbirds.pro.ui.fragment

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EFragment
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
open class MonitoringListFragment : ListFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private val PROJECTION = arrayOf(
                MonitoringColumns._ID,
                MonitoringColumns.CODE,
                MonitoringColumns.STATUS,
                MonitoringColumns.DATA,
                MonitoringColumns.ENTRIES_COUNT)

        private const val TAG = SmartBirdsApplication.TAG + ".FMonLst"
    }


    private var adapter: CursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
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
        LoaderManager.getInstance(this).restartLoader(0, null, this)
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
        return CursorLoader(requireContext(),
                Monitorings.CONTENT_URI,
                PROJECTION,
                null,
                null,
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

    interface Listener {
        fun onMonitoringSelected(monitoringCode: String?)
    }

    @AfterViews
    protected open fun setupListView() {
        setEmptyText(getText(R.string.emptyList))
    }
}