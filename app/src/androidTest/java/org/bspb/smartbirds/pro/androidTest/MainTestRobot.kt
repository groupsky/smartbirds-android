package org.bspb.smartbirds.pro.androidTest

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R

@TestRobotMarker
private interface MainRobot

class MainTestRobot : MainRobot {

    companion object {

        fun mainScreen(block: MainTestRobot.() -> Unit): MainTestRobot {
            return MainTestRobot().apply(block)
        }
    }

    fun isDisplayed() = toolbarWithTitle(R.string.app_name).check(matches(ViewMatchers.isDisplayed()))

}