package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.Matchers

@TestRobotMarker
private interface HerptilesFormRobot

class HerptilesFormTestRobot : HerptilesFormRobot {
    companion object {
        fun herptilesScreen(block: HerptilesFormTestRobot.() -> Unit): HerptilesFormTestRobot {
            return HerptilesFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_herptile).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                withText(R.string.menu_entry_save)
            )
        )

    fun tabMain(): ViewInteraction = onView(withText(R.string.tab_required))

    fun tabOptional(): ViewInteraction =
        onView(withText(R.string.tab_optional))

    // Main fields
    fun fieldModeratorReview(): ViewInteraction =
        onView(withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        onView(withText(R.string.monitoring_herptiles_private))

    fun fieldSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_name))

    fun fieldGender(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_gender))

    fun fieldAge(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_age))

    fun fieldCount(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_count))

    fun fieldHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_habitat))

    fun fieldFindings(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herptiles_danger_observation))

    fun fieldMarking(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_marking))

    fun fieldDistanceFromAxis(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_axis_distance))

    fun fieldThreats(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_notes))

    // Optional fields
    fun fieldScl(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_scl))

    fun fieldMpl(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_mpl))

    fun fieldMcw(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_mcw))

    fun fieldHLcap(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_h))

    fun fieldWeight(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_weight))

    fun fieldTempSubstrate(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_t_substrate))

    fun fieldTempAir(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_t_vha))

    fun fieldTempCloaca(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_t_kloaka))

    fun fieldSqVentr(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_sq_ventr))

    fun fieldSqCaud(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_sq_caud))

    fun fieldSqDors(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_herp_sq_dors))
}