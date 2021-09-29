package org.bspb.smartbirds.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.tools.rule.DbRule
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert
import org.junit.Before
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

    @Rule
    @JvmField
    var dbRule = DbRule()

    @Test
    fun observationMethodologyShouldBeHiddenIfNotFinishing() {
        monitoringScreen {
            // Open action bar menu
            openContextualActionModeOverflowMenu()
            // Click on "Common" item
            onView(ViewMatchers.withText(R.string.menu_monitoring_common)).perform(
                click()
            )
        }

        commonFormScreen {
            fieldObservationMethodology().check(matches(not(ViewMatchers.isDisplayed())))
        }
    }

    @Test
    fun observationMethodologyShouldBeVisibleIfFinishing() {
        monitoringScreen {
            onView(ViewMatchers.withText(R.string.menu_monitoring_finish)).perform(
                click()
            )
        }
        commonFormScreen {
            fieldObservationMethodology().check(matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun checkObservationMethodologyIsRequired() {
        monitoringScreen {
            onView(ViewMatchers.withText(R.string.menu_monitoring_finish)).perform(
                click()
            )
        }
        commonFormScreen {
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