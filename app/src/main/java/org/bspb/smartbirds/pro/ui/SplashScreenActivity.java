package org.bspb.smartbirds.pro.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.WindowFeature;
import org.bspb.smartbirds.pro.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WindowFeature({ Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_splash_screen)
public class SplashScreenActivity extends Activity {

    // Duration in milliseconds
    private static final int SPLASH_DURATION = 3000;

    private ScheduledExecutorService executor;

    @AfterViews
    public void postDelayedStart() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                MainActivity_.intent(SplashScreenActivity.this).start();
                finish();
            }
        }, SPLASH_DURATION, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
