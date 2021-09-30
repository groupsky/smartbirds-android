package org.bspb.smartbirds.pro.ui;

import android.os.Bundle;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.fragment.CurrentMonitoringCommonFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.CurrentMonitoringCommonFormFragment_;

/**
 * Created by groupsky on 14-10-21.
 */
@EActivity(R.layout.activity_start_monitoring)
@OptionsMenu(R.menu.monitoring_edit_common_form)
public class EditCurrentCommonFormActivity extends BaseActivity {

    private static final String TAG = SmartBirdsApplication.TAG + ".StartMonitoring";
    CurrentMonitoringCommonFormFragment formFragment;

    @Extra
    boolean isFinishing = false;

    @OptionsMenuItem(R.id.action_submit)
    void setMenuSubmit(MenuItem menuSubmit) {
        if (isFinishing) {
            menuSubmit.setTitle(R.string.menu_monitoring_finish);
        } else {
            menuSubmit.setTitle(R.string.menu_monitoring_save);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataService_.intent(this).start();
    }

    @AfterViews
    void createFragment() {
        formFragment = (CurrentMonitoringCommonFormFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (formFragment == null) {
            formFragment = CurrentMonitoringCommonFormFragment_.builder().isFinishing(isFinishing).build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, formFragment)
                    .commit();
        }

        if (isFinishing) {
            setTitle(R.string.title_activity_finish);
        } else {
            setTitle(R.string.title_activity_edit);
        }
    }

    @OptionsItem(R.id.action_submit)
    void save() {
        if (formFragment.validate()) {
            formFragment.save();
            setResult(RESULT_OK);
            finish();
        }
    }

    @OptionsItem(android.R.id.home)
    void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
