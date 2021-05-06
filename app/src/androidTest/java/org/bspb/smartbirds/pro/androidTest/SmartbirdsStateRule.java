package org.bspb.smartbirds.pro.androidTest;

import android.Manifest;

import androidx.test.rule.GrantPermissionRule;

import org.bspb.smartbirds.pro.androidTest.internal.BatteryNotification;
import org.bspb.smartbirds.pro.androidTest.internal.LoggedIn;
import org.jetbrains.annotations.NotNull;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SmartbirdsStateRule implements TestRule {

    private final Statement statement;

    SmartbirdsStateRule(Statement statement) {
        this.statement = statement;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();

                base.evaluate();
            }
        };
    }

    public static TestRule setLoggedIn (boolean state) {
        return new SmartbirdsStateRule(new LoggedIn(state));
    }

    public static TestRule setBatteryNotification(boolean shown) {
        return new SmartbirdsStateRule(new BatteryNotification(shown));
    }

    public static TestRule grantMonitoringPermissions() {
        return GrantPermissionRule.grant(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );
    }
}
