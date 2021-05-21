package org.bspb.smartbirds.pro.tools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.robot.TestRobotMarker

@TestRobotMarker
private interface MainRobot

class MainTestRobot : MainRobot {

    companion object {

        fun mainScreen(block: MainTestRobot.() -> Unit): MainTestRobot {
            return MainTestRobot().apply(block)
        }
    }

    fun isDisplayed() =
        toolbarWithTitle(R.string.app_name).check(matches(ViewMatchers.isDisplayed()))

    fun startButton() = onView(withId(R.id.btn_start_birds))
    fun resumeButton() = onView(withId(R.id.btn_resume_birds))
    fun cancelButton() = onView(withId(R.id.btn_cancel_birds))
}