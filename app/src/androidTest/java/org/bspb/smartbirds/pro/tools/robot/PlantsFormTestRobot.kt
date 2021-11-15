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
private interface PlantsFormRobot

class PlantsFormTestRobot : PlantsFormRobot {
    companion object {
        fun plantsScreen(block: PlantsFormTestRobot.() -> Unit): PlantsFormTestRobot {
            return PlantsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_plants).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                ViewMatchers.withText(R.string.menu_entry_save)
            )
        )

    fun tabMain(): ViewInteraction = onView(ViewMatchers.withText(R.string.tab_required))

    fun tabOptional(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.tab_optional))

    // Main fields
    fun fieldModeratorReview(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_plants_private))

    fun fieldSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_name))

    fun fieldReportingUnit(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_reporting_unit))

    fun fieldPhenologicalPhase(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_phenological_phase))

    fun fieldCount(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_count))

    fun fieldDensity(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_density))

    fun fieldCover(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_cover))

    fun fieldHabitat(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_habitat))

    fun fieldThreatsAndImpacts(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_threats))

    fun fieldThreats(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldElevation(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_elevation))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_notes))

    // Optional fields
    fun fieldAccompanyingSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_plants_accompanying_species))
}