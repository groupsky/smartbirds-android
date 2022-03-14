package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.Matchers

@TestRobotMarker
private interface PylonsFormRobot

class PylonsFormTestRobot : PylonsFormRobot {
    companion object {
        fun pylonsScreen(block: PylonsFormTestRobot.() -> Unit): PylonsFormTestRobot {
            return PylonsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_pylons).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                ViewMatchers.withText(R.string.menu_entry_save)
            )
        )

    fun fieldModeratorReview(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_birds_private))

    fun fieldPylonType(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_pylon_type))

    fun fieldSpeciesNestOnPylon(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_species_nest_on_pylon))

    fun fieldNestType(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_nest_type))

    fun fieldPylonInsulated(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_pylons_pylon_insulated))

    fun fieldPrimaryHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_primary_habitat))

    fun fieldSecondaryHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_secondary_habitat))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_notes))
}