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
import org.bspb.smartbirds.pro.tools.withIndex
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf

@TestRobotMarker
private interface WetlandZonesFormRobot

class WetlandZonesFormTestRobot : WetlandZonesFormRobot {

    companion object {
        fun wetlandScreen(block: WetlandZonesFormTestRobot.() -> Unit): WetlandZonesFormTestRobot {
            return WetlandZonesFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_humid).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun buttonSave(): ViewInteraction =
        onView(allOf(instanceOf(Button::class.java), withText(R.string.menu_entry_save)))

    fun fieldSpecies(index: Int): ViewInteraction =
        onView(withIndex(withHintParentOrOwn(R.string.monitoring_birds_name), index))


    fun fieldCount(index: Int): ViewInteraction =
        onView(withIndex(withHintParentOrOwn(R.string.monitoring_birds_count), index))

    fun fieldGender(index: Int): ViewInteraction =
        onView(withIndex(withHintParentOrOwn(R.string.monitoring_birds_gender), index))

    fun fieldAge(index: Int): ViewInteraction =
        onView(withIndex(withHintParentOrOwn(R.string.monitoring_birds_age), index))
}