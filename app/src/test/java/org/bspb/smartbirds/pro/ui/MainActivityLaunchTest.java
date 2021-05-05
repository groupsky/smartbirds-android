package org.bspb.smartbirds.pro.ui;

import static androidx.test.espresso.intent.VerificationModes.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static org.junit.Assert.assertEquals;
import static androidx.test.ext.truth.content.IntentSubject.assertThat;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.*;
import static org.junit.Assert.assertNotEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.common.collect.Iterables;

import org.bspb.smartbirds.pro.test.EspressoIntentsRule;
import org.bspb.smartbirds.pro.test.SmartbirdsScenario;
import org.bspb.smartbirds.pro.test.SmartbirdsState;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class MainActivityLaunchTest {

    @Rule
    public EspressoIntentsRule espressoIntentsCapturing = new EspressoIntentsRule();

    @Test
    public void startsLoginActivityWhenNotLoggedIn() {
        // prepare the app in appropriate state
        SmartbirdsScenario.setupState(SmartbirdsState.FRESH);

        // use espresso scenario helper to launch the activity
        ActivityScenario<MainActivity_> activityScenario =
                ActivityScenario.launch(MainActivity_.class);

        // An intent is fired to launch LoginActivity. Robolectric doesn't currently
        // support launching a new Activity, so use Espresso Intents to verify intent was sent
        intended(hasComponent(hasClassName(LoginActivity_.class.getName())));
    }

    @Test
    public void displaysMainActivityWhenLoggedIn() {
        // prepare the app in appropriate state
        SmartbirdsScenario.setupState(SmartbirdsState.AUTHENTICATED);

        // use espresso scenario helper to launch the activity
        ActivityScenario<MainActivity_> activityScenario =
                ActivityScenario.launch(MainActivity_.class);

        // activity should have been displayed
        assertEquals(Lifecycle.State.RESUMED, activityScenario.getState());

        // No internal intents fired
        intended(isInternal(), times(0));
    }

    @Test
    public void startsMonitoringActivityWhenInMonitoring() {
        // prepare the app in appropriate state
        SmartbirdsScenario.setupState(SmartbirdsState.MONITORING);

        // use espresso scenario helper to launch the activity
        ActivityScenario<MainActivity_> activityScenario =
                ActivityScenario.launch(MainActivity_.class);

        // An intent is fired to launch MonitoringActivity. Robolectric doesn't currently
        // support launching a new Activity, so use Espresso Intents to verify intent was sent
        intended(hasComponent(hasClassName(MonitoringActivity_.class.getName())));
    }
}
