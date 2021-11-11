package org.bspb.smartbirds.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommonFormTest {

    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Test
    fun checkObservationMethodologyIsRequired() {
        monitoringScreen {
            onView(ViewMatchers.withText(R.string.menu_monitoring_finish)).perform(
                click()
            )
        }
        commonFormScreen {
            fieldObservationMethodology().perform(click())
            singleChoiceDialog {
                clearSelection()
            }
            buttonFinish().perform(click())
            fieldObservationMethodology().check(
                matches(
                    ViewMatchers.hasErrorText(
                        ApplicationProvider.getApplicationContext<Context>()
                            .getString(R.string.required_field)
                    )
                )
            )
        }
    }

}