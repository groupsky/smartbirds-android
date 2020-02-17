package org.bspb.smartbirds.pro.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.service.DataService_;

@WindowFeature({Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_splash_screen)
public class SplashScreenActivity extends BaseActivity implements Runnable {

    @ViewById(android.R.id.content)
    View content;

    @Pref
    SmartBirdsPrefs_ prefs;

    // Duration in milliseconds
    private static final int SPLASH_DURATION = 3000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        content.postDelayed(this, SPLASH_DURATION);
    }

    @Override
    protected void onPause() {
        content.removeCallbacks(this);
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    public void run() {
        if (prefs.runningMonitoring().get()) {
            DataService_.intent(this).start();
            TaskStackBuilder.create(SplashScreenActivity.this)
                    .addNextIntentWithParentStack(MonitoringActivity_.intent(SplashScreenActivity.this).get())
                    .startActivities();
        } else {
            MainActivity_.intent(SplashScreenActivity.this).start();
        }

        finish();
    }
}
