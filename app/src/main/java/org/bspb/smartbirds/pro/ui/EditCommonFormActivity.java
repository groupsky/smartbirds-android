package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment_;

/**
 * Created by groupsky on 14-10-21.
 */
@EActivity(R.layout.activity_start_monitoring)
@OptionsMenu(R.menu.monitoring_edit_common_form)
public class EditCommonFormActivity extends Activity {

    private static final String TAG = SmartBirdsApplication.TAG + ".StartMonitoring";
    MonitoringCommonFormFragment formFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataService_.intent(this).start();
    }

    @AfterViews
    void createFragment() {
        formFragment = (MonitoringCommonFormFragment) getFragmentManager().findFragmentById(R.id.container);
        if (formFragment == null) {
            formFragment = MonitoringCommonFormFragment_.builder().build();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, formFragment)
                    .commit();
        }
    }

    @OptionsItem(R.id.action_submit)
    void save() {
        if (formFragment.validate()) {
            formFragment.save();
            finish();
        }
    }

    @OptionsItem(android.R.id.home)
    void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
