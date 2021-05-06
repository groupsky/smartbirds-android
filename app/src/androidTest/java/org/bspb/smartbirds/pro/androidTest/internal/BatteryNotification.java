package org.bspb.smartbirds.pro.androidTest.internal;

import androidx.test.core.app.ApplicationProvider;

import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_;
import org.bspb.smartbirds.pro.prefs.UserPrefs_;
import org.junit.runners.model.Statement;

/**
 * Test helper to set logged in or logged out state - it is tightly coupled with how the app
 * handles the authentication so the tests are not
 */
public class BatteryNotification extends Statement {
    private final boolean shown;

    public BatteryNotification(boolean shown) {
        this.shown = shown;
    }

    @Override
    public void evaluate() throws Throwable {
        new SmartBirdsPrefs_(ApplicationProvider.getApplicationContext())
                .edit()
                .isBatteryOptimizationDialogShown().put(shown)
                .apply();

    }
}
