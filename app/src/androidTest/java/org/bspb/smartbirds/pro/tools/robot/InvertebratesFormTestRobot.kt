package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.Matchers

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

    fun buttonSave(): ViewInteraction =
        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                ViewMatchers.withText(R.string.menu_entry_save)
            )
        )

    fun fieldModeratorReview(): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(R.string.monitoring_invertebrates_private))

    fun fieldSpecies(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_name))

    fun fieldGender(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_gender))

    fun fieldAge(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_age))

    fun fieldCount(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_count))

    fun fieldHabitat(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_habitat))

    fun fieldFindings(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_invertebrates_danger_observation))

    fun fieldMarking(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_marking))

    fun fieldThreats(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldNotes(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_notes))
}