package org.bspb.smartbirds.pro.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.test.SmartbirdsState;
import org.bspb.smartbirds.pro.test.SmartbirdsStateRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MonitoringActivityTest {

    @Rule
    public SmartbirdsStateRule smartbirdsStateRule = new SmartbirdsStateRule(SmartbirdsState.MONITORING);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Rule(order = 1)
    public ActivityScenarioRule<MonitoringActivity_> activityScenarioRule = new ActivityScenarioRule<MonitoringActivity_>(MonitoringActivity_.class);

    @Test
    public void startsMonitoringActivityWhenStartBtnClicked() {

    }

}
