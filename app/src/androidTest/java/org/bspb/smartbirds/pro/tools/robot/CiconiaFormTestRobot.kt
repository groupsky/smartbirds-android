package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.Matchers

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

    fun fieldSubstrate(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_substratum))

    fun fieldPylon(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_column))

    fun fieldNestArtificial(): ViewInteraction =
        onView(withHint(R.string.monitoring_ciconia_nest_artificial))

    fun fieldPylonType(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_column_type))

    fun fieldTree(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_tree))

    fun fieldBuilding(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_building))

    fun fieldNestArtificialHuman(): ViewInteraction =
        onView(withHint(R.string.monitoring_ciconia_nest_artificial_human))

    fun fieldNestAnotherSubstrate(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_nest_other))

    fun fieldNestThisYearNotUtilizedByWhiteStorks(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_not_occupied))

    fun fieldThisYearOneTwoBirdsAppearedInNest(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_new_birds))

    fun fieldApproximateDateStorksAppeared(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_date_appear))

    fun fieldApproximateDateDisappearanceWhiteStorks(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_disappear))

    fun fieldThisYearInTheNestAppeared(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_this_year))

    fun fieldCountJuvenilesInNest(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_small_count))

    fun fieldNestNotUsedForOverOneYear(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_not_occupied_more_than_year))

    fun fieldDataOnJuvenileMortalityFromElectrocutions(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_small_death_electricity))

    fun fieldDataOnJuvenilesExpelledFromParents(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_small_death_from_parent))

    fun fieldDiedOtherReasons(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_small_death_other))

    fun fieldCause(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_cause))

    fun fieldThreats(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_ciconia_notes))

    fun buttonSave(): ViewInteraction =
        onView(
            Matchers.allOf(
                Matchers.instanceOf(Button::class.java),
                withText(R.string.menu_entry_save)
            )
        )


}