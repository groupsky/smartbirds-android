package org.bspb.smartbirds.pro.test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.bspb.smartbirds.pro.ui.LoginActivity_;
import org.bspb.smartbirds.pro.ui.MainActivity_;
import org.bspb.smartbirds.pro.ui.MonitoringActivity_;
import org.bspb.smartbirds.pro.ui.MonitoringDetailActivity;
import org.bspb.smartbirds.pro.ui.SplashScreenActivity_;

import static androidx.test.internal.util.Checks.checkNotNull;
import static org.bspb.smartbirds.pro.test.SmartbirdsState.FRESH;
import static org.bspb.smartbirds.pro.test.SmartbirdsState.MONITORING;

public class SmartbirdsScenario {

    private ActivityScenario<? extends Activity> activityScenario;
    private SmartbirdsState desiredState;

    public SmartbirdsScenario(SmartbirdsState state) {
        desiredState = state;
        setupState(state);
        final Class<? extends Activity> activityClass = getDefaultActivityForState(state);
        activityScenario = ActivityScenario.launch(activityClass);
    }

    public static void setupState(SmartbirdsState state) {
        UserPrefs_.UserPrefsEditor_ userPrefsEditor = new UserPrefs_(ApplicationProvider.getApplicationContext())
                .edit().clear();
        SmartBirdsPrefs_.SmartBirdsPrefsEditor_ globalPrefsEditor = new SmartBirdsPrefs_(ApplicationProvider.getApplicationContext())
                .edit().clear();

        userPrefsEditor.isAuthenticated().put(state != FRESH);
        globalPrefsEditor.runningMonitoring().put(state == MONITORING);

        userPrefsEditor.apply();
        globalPrefsEditor.apply();
    }

    // TODO: find a way to get it from AndroidManifest
    private Class<? extends Activity> getLaunchActivity() {
        return SplashScreenActivity_.class;
    }

    private Class<? extends Activity> getDefaultActivityForState(SmartbirdsState state) {
        switch (state) {
            case FRESH:
                return LoginActivity_.class;
            case MONITORING:
                return MonitoringActivity_.class;
            case AUTHENTICATED:
                return MainActivity_.class;
            default:
                throw new RuntimeException("Unhandled smartbirds state " + state);
        }
    }

    public static SmartbirdsScenario launch(SmartbirdsState state) {
        SmartbirdsScenario scenario = new SmartbirdsScenario(checkNotNull(state));
        return scenario;
    }

    public void close() {

    }
}
