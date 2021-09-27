package org.bspb.smartbirds.pro.tools.robot

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn

@TestRobotMarker
private interface ThreatsFormRobot

class ThreatsFormTestRobot : ThreatsFormRobot {
    companion object {
        fun threatsScreen(block: ThreatsFormTestRobot.() -> Unit): ThreatsFormTestRobot {
            return ThreatsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_threats).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun fieldThreatTypePoison(@StringRes typeResId: Int): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(typeResId))
}