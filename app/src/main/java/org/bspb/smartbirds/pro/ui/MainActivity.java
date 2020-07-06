package org.bspb.smartbirds.pro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.events.EEventBus;
import org.bspb.smartbirds.pro.events.LogoutEvent;
import org.bspb.smartbirds.pro.events.ResumeMonitoringEvent;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.ui.fragment.MainFragment_;


@WindowFeature({Window.FEATURE_INDETERMINATE_PROGRESS})
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @Bean
    EEventBus bus;

    @Pref
    UserPrefs_ prefs;

    @Pref
    SmartBirdsPrefs_ globalPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireAuthentication();
        if (!isFinishing() && globalPrefs.runningMonitoring().get()) {
            MonitoringActivity_.intent(this).start();
        }
    }

    @AfterViews
    void createFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null)
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment_.builder().build())
                    .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requireAuthentication();
        bus.register(this);
    }

    @Override
    protected void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requireAuthentication();
    }

    public void onEvent(StartMonitoringEvent event) {
        StartMonitoringActivity_.intent(this).start();
    }

    public void onEvent(ResumeMonitoringEvent event) {
        MonitoringActivity_.intent(this).start();
    }

    public void onEvent(LogoutEvent event) {
        requireAuthentication();
    }

    @AfterInject
    protected void requireAuthentication() {
        if (!prefs.isAuthenticated().get() && !isFinishing()) {
            startActivity(new Intent(this, LoginActivity_.class));
            finish();
        }
    }

}
