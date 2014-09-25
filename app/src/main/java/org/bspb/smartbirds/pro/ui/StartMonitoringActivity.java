package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.Trace;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent;
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.ui.fragment.MainFragment_;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment_;

@EActivity(R.layout.activity_start_monitoring)
public class StartMonitoringActivity extends Activity  {

    private static final String TAG = SmartBirdsApplication.TAG+".StartMonitoring";
    @Bean EEventBus bus;

    @AfterViews
    void createFragment() {
        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, MonitoringCommonFormFragment_.builder().build())
                    .commit();
    }

    public void onEvent(MonitoringStartedEvent event) {
        Log.d(TAG, "onMonitoringStartedEvent");
        bus.removeStickyEvent(StartMonitoringEvent.class);
        bus.removeStickyEvent(MonitoringStartedEvent.class);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        bus.registerSticky(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelMonitoring();
    }

    private void cancelMonitoring() {
        Log.d(TAG, "cancelMonitoring");
        bus.removeStickyEvent(StartMonitoringEvent.class);
        bus.removeStickyEvent(MonitoringStartedEvent.class);
        bus.post(new CancelMonitoringEvent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            cancelMonitoring();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEvent(SetMonitoringCommonData event) {
        MonitoringActivity_.intent(this).start();
        finish();
    }
}
