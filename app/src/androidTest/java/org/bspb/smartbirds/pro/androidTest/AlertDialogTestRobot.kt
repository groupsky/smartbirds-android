package org.bspb.smartbirds.pro.androidTest

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText

@TestRobotMarker
private interface AlertDialogRobot

class AlertDialogTestRobot : AlertDialogRobot {

    companion object {

        fun alertDialog(block: AlertDialogTestRobot.() -> Unit): AlertDialogTestRobot {
            return AlertDialogTestRobot().apply(block)
        }
    }

    fun isDisplayed(@StringRes resource: Int) =
        onView(withText(resource)).check(matches(ViewMatchers.isDisplayed()))

    fun button1() = onView(withId(android.R.id.button1))
    fun button2() = onView(withId(android.R.id.button2))
    fun button3() = onView(withId(android.R.id.button3))
}