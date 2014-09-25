package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.ui.fragment.MainFragment_;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment;
import org.bspb.smartbirds.pro.ui.fragment.MonitoringCommonFormFragment_;

@EActivity(R.layout.activity_start_monitoring)
public class StartMonitoringActivity extends Activity  {

    @AfterViews
    void createFragment() {
        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, MonitoringCommonFormFragment_.builder().build())
                    .commit();
    }

}
