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
private interface PylonsCasualtiesFormRobot

class PylonsCasualtiesFormTestRobot : PylonsCasualtiesFormRobot {
    companion object {
        fun pylonsCasualtiesScreen(block: PylonsCasualtiesFormTestRobot.() -> Unit): PylonsCasualtiesFormTestRobot {
            return PylonsCasualtiesFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_pylons_casualties).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

    fun fieldSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_name))

    fun fieldCount(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_count))

    fun fieldAge(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_age))

    fun fieldGender(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_gender))

    fun fieldCauseOfDeath(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_cause_of_death))

    fun fieldBodyCondition(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_body_condition))

    fun fieldPrimaryHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_primary_habitat))

    fun fieldSecondaryHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_casualties_secondary_habitat))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_pylons_notes))
}