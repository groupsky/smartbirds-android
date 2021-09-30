package org.bspb.smartbirds.pro.tools.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn

@TestRobotMarker
private interface CiconiaFormRobot

class CiconiaFormTestRobot : CiconiaFormRobot {
    companion object {
        fun ciconiaScreen(block: CiconiaFormTestRobot.() -> Unit): CiconiaFormTestRobot {
            return CiconiaFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_ciconia).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun fieldNotes(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_ciconia_notes))
}