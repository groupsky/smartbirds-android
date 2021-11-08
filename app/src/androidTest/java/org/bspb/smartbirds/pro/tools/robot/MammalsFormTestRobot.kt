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
private interface MammalsFormRobot

class MammalsFormTestRobot : MammalsFormRobot {
    companion object {
        fun mammalsScreen(block: MammalsFormTestRobot.() -> Unit): MammalsFormTestRobot {
            return MammalsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_mammal).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        Espresso.onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                ViewMatchers.withText(R.string.menu_entry_save)
            )
        )

    fun tabMain(): ViewInteraction = Espresso.onView(ViewMatchers.withText(R.string.tab_required))

    fun tabOptional(): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(R.string.tab_optional))

    // Main fields
    fun fieldModeratorReview(): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        Espresso.onView(ViewMatchers.withText(R.string.monitoring_mammal_private))

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
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_mammal_danger_observation))

    fun fieldMarking(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_marking))

    fun fieldDistanceFromAxis(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_axis_distance))

    fun fieldThreats(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldNotes(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_notes))

    // Optional fields
    fun fieldL(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_mammal_L))

    fun fieldC(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_mammal_C))

    fun fieldA(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_mammal_A))

    fun fieldPl(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_mammal_Pl))

    fun fieldWeight(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_weight))

    fun fieldTempSubstrate(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_t_substrate))

    fun fieldTempAir(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_herp_t_vha))
}