package org.bspb.smartbirds.pro.ui;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.enums.EntryType;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.EntrySubmitted;

/**
 * Created by groupsky on 16.03.17.
 */

@EActivity(R.layout.activity_form)
public class EditMonitoringEntryActivity extends BaseActivity {

    @Extra
    long entryId;
    @Extra
    EntryType entryType;
    @FragmentById(R.id.container)
    Fragment fragment;
    @Bean
    EEventBus eventBus;

    @AfterViews
    protected void setupFragment() {
        if (entryId <= 0 || entryType == null) {
            this.finish();
            return;
        }
        if (fragment == null) {
            fragment = entryType.loadFragment(entryId);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEvent(EntrySubmitted event) {
        setResult(RESULT_OK);
        finish();
    }
}
