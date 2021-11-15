package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.Matchers

@TestRobotMarker
private interface ThreatsFormRobot

class ThreatsFormTestRobot : ThreatsFormRobot {
    companion object {
        fun threatsScreen(block: ThreatsFormTestRobot.() -> Unit): ThreatsFormTestRobot {
            return ThreatsFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_threats).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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

    fun fieldModeratorReview(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_moderator_review))

    fun fieldConfidential(): ViewInteraction =
        onView(ViewMatchers.withText(R.string.monitoring_birds_private))

    fun fieldThreatTypePoison(@StringRes typeResId: Int): ViewInteraction =
        onView(ViewMatchers.withText(typeResId))

    fun fieldCategory(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_category))

    fun fieldClass(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_class))

    fun fieldSpecies(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_species))

    fun fieldCount(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_count))

    fun fieldEstimate(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_estimate))

    fun fieldPoisoning(@StringRes typeResId: Int): ViewInteraction =
        onView(ViewMatchers.withText(typeResId))

    fun fieldStateCarcass(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_state_carcass))

    fun fieldSampleTaken1(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_taken_1))

    fun fieldSampleTaken2(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_taken_2))

    fun fieldSampleTaken3(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_taken_3))

    fun fieldSampleCode1(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_code_1))

    fun fieldSampleCode2(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_code_2))

    fun fieldSampleCode3(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_sample_code_3))

    fun fieldNotes(): ViewInteraction =
        onView(withHintParentOrOwn(R.string.monitoring_threats_notes))

}