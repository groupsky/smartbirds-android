package org.bspb.smartbirds.pro.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringEntryListFragment_;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringListFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringListFragment_;

/**
 * An activity representing a list of Monitorings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MonitoringDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@EActivity(R.layout.activity_monitoring_list)
public class MonitoringListActivity extends BaseActivity implements MonitoringListFragment.Listener, MonitoringEntryListFragment.Listener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @FragmentById(R.id.monitoring_list_container)
    MonitoringListFragment listFragment;

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
    protected void detectScreen() {
        if (findViewById(R.id.monitoring_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @AfterViews
    protected void setupListFragment() {
        if (listFragment == null) {
            listFragment = MonitoringListFragment_.builder().build();
            getFragmentManager().beginTransaction().replace(R.id.monitoring_list_container, listFragment).commit();
        }
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMonitoringSelected(String monitoringCode) {
        if (mTwoPane) {
            MonitoringEntryListFragment fragment = MonitoringEntryListFragment_.builder().setMonitoringCode(monitoringCode).build();
            getFragmentManager().beginTransaction().replace(R.id.monitoring_detail_container, fragment).commit();
        } else {
            MonitoringDetailActivity_.intent(this).start();
        }
    }

    @Override
    public void onMonitoringEntrySelected(long id, EntryType entryType) {
        EditMonitoringEntryActivity_.intent(this).entryId(id).entryType(entryType).start();
    }
}
