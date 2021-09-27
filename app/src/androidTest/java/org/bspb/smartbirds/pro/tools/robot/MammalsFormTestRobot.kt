package org.bspb.smartbirds.pro.tools.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn

@TestRobotMarker
private interface MammalsFormRobot

class MammalsFormTestRobot : MammalsFormRobot {
    companion object {
        fun mammalsScreen(block: MammalsFormTestRobot.() -> Unit): MammalsFormTestRobot {
            return MammalsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_mammal).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun fieldNotes(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_notes))
}