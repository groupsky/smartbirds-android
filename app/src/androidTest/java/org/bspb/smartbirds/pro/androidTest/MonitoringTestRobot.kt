package org.bspb.smartbirds.pro.androidTest

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.bspb.smartbirds.pro.R

@TestRobotMarker
private interface MonitoringRobot

class MonitoringTestRobot : MonitoringRobot {

    companion object {

        fun monitoringScreen(block: MonitoringTestRobot.() -> Unit): MonitoringTestRobot {
            return MonitoringTestRobot().apply(block)
        }
    }

    fun isDisplayed() = toolbarWithTitle(R.string.title_activity_monitoring).check(matches(ViewMatchers.isDisplayed()))
    fun navigateUpButton() = onView(withContentDescription(R.string.abc_action_bar_up_description))
}
