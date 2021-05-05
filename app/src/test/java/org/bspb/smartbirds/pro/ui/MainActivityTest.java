package org.bspb.smartbirds.pro.ui;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.bspb.smartbirds.pro.R;
import org.bspb.smartbirds.pro.SmartBirdsApplication;
import org.bspb.smartbirds.pro.events.EEventBus_;
import org.bspb.smartbirds.pro.events.StartMonitoringEvent;
import org.bspb.smartbirds.pro.test.EspressoIntentsRule;
import org.bspb.smartbirds.pro.test.SmartbirdsState;
import org.bspb.smartbirds.pro.test.SmartbirdsStateRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule(order = -5)
    public EspressoIntentsRule espressoIntentsCapturing = new EspressoIntentsRule();

    @Rule
    public ActivityScenarioRule<MainActivity_> activityScenarioRule = new ActivityScenarioRule<MainActivity_>(MainActivity_.class);

    @Rule(order = -2)
    public SmartbirdsStateRule smartbirdsStateRule = new SmartbirdsStateRule(SmartbirdsState.AUTHENTICATED);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Test
    public void startsMonitoringActivityWhenStartBtnClicked() {
        System.out.println("Starting test");
        onView(withText("Start"))
                .check(matches(allOf(
                        isClickable(),
                        isEnabled()
                )))
                .perform(click());

        onView(withId(R.id.btn_upload)).perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.main_screen_btn_browse))
                .perform(click());

//        activityScenarioRule.getScenario().onActivity(activity -> {
//            // An intent is fired to launch MonitoringActivity. Robolectric doesn't currently
//            // support launching a new Activity, so use Espresso Intents to verify intent was sent
//            intended(hasComponent(hasClassName(MonitoringActivity_.class.getName())));
//        });
//
//        // An intent is fired to launch MonitoringActivity. Robolectric doesn't currently
//        // support launching a new Activity, so use Espresso Intents to verify intent was sent
//        intended(hasComponent(hasClassName(MonitoringActivity_.class.getName())));
//        assertNotNull(EEventBus_.getInstance_(ApplicationProvider.getApplicationContext()).getStickyEvent(StartMonitoringEvent.class));
    }
}
