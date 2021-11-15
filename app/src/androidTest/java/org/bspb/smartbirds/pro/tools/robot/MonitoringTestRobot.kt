package org.bspb.smartbirds.pro.tools.robot

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.hamcrest.Matchers

@TestRobotMarker
private interface MonitoringRobot

class MonitoringTestRobot : MonitoringRobot {

    companion object {

        fun monitoringScreen(block: MonitoringTestRobot.() -> Unit): MonitoringTestRobot {
            return MonitoringTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.title_activity_monitoring).check(matches(ViewMatchers.isDisplayed()))

    fun buttonUp(): ViewInteraction =
        onView(withContentDescription(R.string.abc_action_bar_up_description))

    fun buttonFabAddEntry(): ViewInteraction = onView(withText(R.string.menu_monitoring_new_entry))

    fun buttonFinish(): ViewInteraction = onView(withText(R.string.menu_monitoring_finish))

    fun monitoringType(@StringRes resId: Int): DataInteraction =
        Espresso.onData(
            Matchers.`is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(resId)
            )
        )

    fun openNewEntryForm(@StringRes resId: Int) {
        buttonFabAddEntry().perform(ViewActions.click())

        monitoringType(resId).perform(ViewActions.click())
    }
}