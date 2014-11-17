package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartingUpload;
import org.bspb.smartbirds.pro.events.UploadCompleted;
import org.bspb.smartbirds.pro.ui.fragment.MainFragment_;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @Bean
    EEventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @AfterViews
    void createFragment() {
        if (getFragmentManager().findFragmentById(R.id.container) == null)
            getFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment_.builder().build())
                    .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    public void onEvent(StartMonitoringEvent event) {
        StartMonitoringActivity_.intent(this).start();
    }

    public void onEvent(StartingUpload event) {
        setProgressBarIndeterminateVisibility(true);
    }

    public void onEvent(UploadCompleted event) {
        setProgressBarIndeterminateVisibility(false);
    }
}
