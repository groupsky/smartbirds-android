package org.bspb.smartbirds.pro.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.fragment.StatsFragment_;

@EActivity(R.layout.activity_stats)
public class StatsActivity extends BaseActivity {

    @AfterViews
    void createFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, StatsFragment_.builder().build())
                    .commit();
    }
}
