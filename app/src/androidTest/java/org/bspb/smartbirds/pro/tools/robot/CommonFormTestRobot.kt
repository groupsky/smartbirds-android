package org.bspb.smartbirds.pro.tools.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle

@TestRobotMarker
private interface CommonFormRobot

class CommonFormTestRobot : CommonFormRobot {

    companion object {

        fun commonFormScreen(block: CommonFormTestRobot.() -> Unit): CommonFormTestRobot {
            return CommonFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.title_activity_start_monitoring).check(matches(ViewMatchers.isDisplayed()))

    fun fieldSource(): ViewInteraction = onView(withHint(R.string.monitoring_common_source))
    fun buttonSubmit(): ViewInteraction = onView(withId(R.id.action_submit))

    fun buttonFinish(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.menu_monitoring_finish))

    fun fieldObservationMethodology(): ViewInteraction =
        onView(withHint(R.string.monitoring_common_observation_methodology))
}