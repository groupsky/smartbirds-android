package org.bspb.smartbirds.pro.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.service.DataService_;
import org.bspb.smartbirds.pro.ui.utils.NotificationUtils;

import java.util.concurrent.ScheduledExecutorService;

@WindowFeature({Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_splash_screen)
public class SplashScreenActivity extends AppCompatActivity implements Runnable {

    @ViewById(android.R.id.content)
    View content;

    @Pref
    SmartBirdsPrefs_ prefs;

    // Duration in milliseconds
    private static final int SPLASH_DURATION = 3000;

    private ScheduledExecutorService executor;


    @Override
    protected void onResume() {
        super.onResume();
        content.postDelayed(this, SPLASH_DURATION);
        NotificationUtils.hideMonitoringNotification(this);
    }

    @Override
    protected void onPause() {
        content.removeCallbacks(this);
        super.onPause();
    }

    @Override
    public void run() {
        if (prefs.runningMonitoring().get()) {
            DataService_.intent(this).start();
            startActivities(new Intent[]{MainActivity_.intent(SplashScreenActivity.this).get(), MonitoringActivity_.intent(SplashScreenActivity.this).get()});
        } else {
            MainActivity_.intent(SplashScreenActivity.this).start();
        }

        finish();
    }
}
