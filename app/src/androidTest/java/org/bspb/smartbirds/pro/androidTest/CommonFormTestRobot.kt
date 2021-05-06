package org.bspb.smartbirds.pro.androidTest

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.bspb.smartbirds.pro.R

@TestRobotMarker
private interface CommonFormRobot

class CommonFormTestRobot : CommonFormRobot {

    companion object {

        fun commonFormScreen(block: CommonFormTestRobot.() -> Unit): CommonFormTestRobot {
            return CommonFormTestRobot().apply(block)
        }
    }

    fun isDisplayed() = toolbarWithTitle(R.string.title_activity_start_monitoring).check(matches(ViewMatchers.isDisplayed()))
    fun sourceField() = onView(withHint(R.string.monitoring_common_source))
    fun submitButton() = onView(withId(R.id.action_submit))
}
