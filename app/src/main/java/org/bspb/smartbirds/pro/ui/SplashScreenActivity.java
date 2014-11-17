package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.ui.utils.NotificationUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WindowFeature({ Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_splash_screen)
public class SplashScreenActivity extends Activity implements Runnable {

    @ViewById(android.R.id.content)
    View content;

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
        MainActivity_.intent(SplashScreenActivity.this).start();
        finish();
    }
}
