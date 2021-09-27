package org.bspb.smartbirds.pro.tools.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn

@TestRobotMarker
private interface InvertebratesFormRobot

class InvertebratesFormTestRobot : InvertebratesFormRobot {
    companion object {
        fun invertebratesScreen(block: InvertebratesFormTestRobot.() -> Unit): InvertebratesFormTestRobot {
            return InvertebratesFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_invertebrates).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

    fun fieldNotes(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_notes))
}