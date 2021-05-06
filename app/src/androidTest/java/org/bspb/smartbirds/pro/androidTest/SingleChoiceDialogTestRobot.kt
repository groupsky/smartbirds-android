package org.bspb.smartbirds.pro.androidTest

import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.bspb.smartbirds.pro.ui.views.SingleChoiceFormInput
import org.hamcrest.CoreMatchers.instanceOf

@TestRobotMarker
private interface SingleChoiceDialogRobot

class SingleChoiceDialogTestRobot : SingleChoiceDialogRobot {

    companion object {

        fun singleChoiceDialog(block: SingleChoiceDialogTestRobot.() -> Unit): SingleChoiceDialogTestRobot {
            return SingleChoiceDialogTestRobot().apply(block)
        }
    }

    fun isDisplayed(@StringRes resource: Int) =
        onView(withText(resource)).check(matches(ViewMatchers.isDisplayed()))

    fun listItem(idx: Int) = onData(instanceOf(SingleChoiceFormInput.NomenclatureItem::class.java)).atPosition(idx)
}