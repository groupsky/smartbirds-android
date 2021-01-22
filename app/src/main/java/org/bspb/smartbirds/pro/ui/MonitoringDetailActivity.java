package org.bspb.smartbirds.pro.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.core.app.NavUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.content.Monitoring;
import org.bspb.smartbirds.pro.content.MonitoringManager;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.fragment.BrowseMonitoringEntryListFragment;
import org.bspb.smartbirds.pro.ui.fragment.BrowseMonitoringEntryListFragment_;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment_;

/**
 * An activity representing a single Monitoring detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MonitoringListActivity}.
 */
@EActivity(R.layout.activity_monitoring_detail)
public class MonitoringDetailActivity extends BaseActivity implements MonitoringEntryListFragment_.Listener {

    @Extra
    String monitoringCode;

    @FragmentById(R.id.monitoring_detail_container)
    MonitoringEntryListFragment fragment;

    @Bean
    MonitoringManager monitoringManager;

    private Monitoring monitoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the Up button in the action bar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @AfterViews
    protected void setupFragment() {
        if (fragment == null) {
            BrowseMonitoringEntryListFragment fragment = BrowseMonitoringEntryListFragment_.builder().setMonitoringCode(monitoringCode).build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.monitoring_detail_container, fragment)
                    .commit();
        }
    }

    @AfterInject
    protected void loadMonitoring() {
        monitoring = monitoringManager.getMonitoring(monitoringCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MonitoringListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMonitoringEntrySelected(long id, EntryType entryType) {
        if (monitoring != null && Monitoring.Status.uploaded.equals(monitoring.status)) {
            ViewMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start();
        } else {
            EditMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start();
        }
    }
}
