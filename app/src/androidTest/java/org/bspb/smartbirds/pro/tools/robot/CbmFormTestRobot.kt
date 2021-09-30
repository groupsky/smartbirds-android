package org.bspb.smartbirds.pro.tools.robot

import android.widget.Button
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.toolbarWithTitle
import org.bspb.smartbirds.pro.tools.withHintParentOrOwn
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything

@TestRobotMarker
public interface CbmFormRobot

class CbmFormTestRobot : CbmFormRobot {
    companion object {
        fun cbmScreen(block: CbmFormTestRobot.() -> Unit): CbmFormTestRobot {
            return CbmFormTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.entry_type_cbm).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    fun fieldSpecies(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_cbm_name))

    fun quickChoiceButtons(): List<ViewInteraction> = listOf(
        Espresso.onView(withId(R.id.quick_1)),
        Espresso.onView(withId(R.id.quick_2)),
        Espresso.onView(withId(R.id.quick_3)),
        Espresso.onView(withId(R.id.quick_4)),
        Espresso.onView(withId(R.id.quick_5)),
        Espresso.onView(withId(R.id.quick_6))
    )

    fun buttonSave(): ViewInteraction =
        Espresso.onView(
            allOf(
                instanceOf(Button::class.java),
                ViewMatchers.withText(R.string.menu_entry_save)
            )
        )

    fun fieldCount(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_cbm_count))

    fun fieldThreats(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_common_threats))

    fun fieldZone(): ViewInteraction =
        Espresso.onView(withHintParentOrOwn(R.string.monitoring_cbm_zone))

    fun fillRequiredFields() {
        // select species
        fieldSpecies().perform(click())
        SingleChoiceDialogTestRobot.singleChoiceDialog {
            listItem(0).perform(click())
        }

        // enter count
        fieldCount().perform(scrollTo(), typeText("1"))

        fieldZone().perform(scrollTo(), click())
        SingleChoiceDialogTestRobot.singleChoiceDialog {
            onData(anything()).atPosition(0).perform(click())
        }

        // set distance field
        Espresso.onView(ViewMatchers.withText("1 - (0 - 25 m)")).perform(click())
    }
}