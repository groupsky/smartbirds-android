package org.bspb.smartbirds.pro.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.androidannotations.annotations.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.ui.fragment.BrowseMonitoringEntryListFragment_
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment
import org.bspb.smartbirds.pro.utils.MonitoringManager

/**
 * An activity representing a single Monitoring detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MonitoringListActivity}.
 */
@EActivity(R.layout.activity_monitoring_detail)
open class MonitoringDetailActivity : BaseActivity(), MonitoringEntryListFragment.Listener {

    @JvmField
    @Extra
    protected var monitoringCode: String? = null

    @JvmField
    @FragmentById(R.id.monitoring_detail_container)
    protected var fragment: MonitoringEntryListFragment? = null

    private var monitoring: Monitoring? = null

    private val monitoringManager = MonitoringManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show the Up button in the action bar.
        val actionBar = actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @AfterViews
    protected open fun setupFragment() {
        if (fragment == null) {
            val fragment = BrowseMonitoringEntryListFragment_.builder().setMonitoringCode(monitoringCode).build()
            supportFragmentManager.beginTransaction()
                .add(R.id.monitoring_detail_container, fragment)
                .commit()
        }
    }

    @AfterInject
    protected open fun loadMonitoring() {
        lifecycleScope.launch {
            monitoring = monitoringManager.getMonitoring(monitoringCode!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, Intent(this, MonitoringListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMonitoringEntrySelected(id: Long, entryType: EntryType?) {
        if (monitoring != null && Monitoring.Status.uploaded == monitoring!!.status) {
            ViewMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start()
        } else {
            EditMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start()
        }
    }
}