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
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf

@TestRobotMarker
private interface BirdsFormRobot

class BirdsFormTestRobot : BirdsFormRobot {

    companion object {
        fun birdsScreen(block: BirdsFormTestRobot.() -> Unit): BirdsFormTestRobot {
            return BirdsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_birds).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        onView(allOf(instanceOf(Button::class.java), withText(R.string.menu_entry_save)))

    fun tabMain(): ViewInteraction = onView(withText(R.string.tab_required))

    fun tabOptional(): ViewInteraction = onView(withText(R.string.tab_optional))

    // Main fields
    fun fieldModeratorReview(): ViewInteraction =
        onView(withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        onView(withText(R.string.monitoring_birds_private))

    fun fieldSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_name))

    fun fieldCountUnit(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_count_unit))

    fun fieldCountType(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_count_type))

    fun fieldCount(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_count))

    fun fieldCountMin(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_min))

    fun fieldCountMax(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_max))

    fun fieldStatus(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_status))

    fun fieldBehavior(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_behaviour))

    fun fieldGender(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_gender))

    fun fieldAge(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_age))

    fun fieldNestType(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_nesting))


    // Additional fields
    fun fieldDeath(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_death))

    fun fieldMarking(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_marking))

    fun fieldSubstrate(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_substrate))

    fun fieldTree(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_tree))

    fun fieldTreeHeight(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_tree_height))

    fun fieldTreeLocation(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_tree_location))

    fun fieldNestHeight(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_nest_height))

    fun fieldNestLocation(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_nest_location))

    fun fieldIncubation(): ViewInteraction =
        onView(withText(R.string.monitoring_birds_incubation))

    fun fieldNumberOfEggs(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_eggs_count))

    fun fieldNumberOfPull(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_small_downy_count))

    fun fieldNumberOfFledglings(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_small_feathered_count))

    fun fieldNumberOfTakeoff(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_takeoff_count))

    fun fieldNestGuarding(): ViewInteraction =
        onView(withText(R.string.monitoring_birds_nest_guard))

    fun fieldAgeOfFemale(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_female_age))

    fun fieldAgeOfMale(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_male_age))

    fun fieldNestSuccess(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_nest_success))

    fun fieldLandUse(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_land_use))

    fun fieldThreats(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_birds_notes))
}