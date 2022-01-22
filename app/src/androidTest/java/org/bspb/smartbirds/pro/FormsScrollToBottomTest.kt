package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.robot.BirdsFormTestRobot.Companion.birdsScreen
import org.bspb.smartbirds.pro.tools.robot.CbmFormTestRobot.Companion.cbmScreen
import org.bspb.smartbirds.pro.tools.robot.CiconiaFormTestRobot.Companion.ciconiaScreen
import org.bspb.smartbirds.pro.tools.robot.HerptilesFormTestRobot.Companion.herptilesScreen
import org.bspb.smartbirds.pro.tools.robot.InvertebratesFormTestRobot.Companion.invertebratesScreen
import org.bspb.smartbirds.pro.tools.robot.MammalsFormTestRobot.Companion.mammalsScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.PlantsFormTestRobot.Companion.plantsScreen
import org.bspb.smartbirds.pro.tools.robot.ThreatsFormTestRobot.Companion.threatsScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
* Test that all forms can be scrolled to bottom and last ordered fields are visible
* */
@RunWith(AndroidJUnit4::class)
class FormsScrollToBottomTest {

    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Test
    fun cbmFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_cbm).perform(click())
        }
        cbmScreen {
            isDisplayed()
            fieldThreats().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun ciconiaFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_ciconia).perform(click())
        }
        ciconiaScreen {
            isDisplayed()
            fieldNotes().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun birdsFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_birds).perform(click())
        }
        birdsScreen {
            isDisplayed()
            fieldNestType().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun herptilesFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_herptile).perform(click())
        }
        herptilesScreen {
            isDisplayed()
            fieldNotes().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun mammalsFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_mammal).perform(click())
        }
        mammalsScreen {
            isDisplayed()
            fieldNotes().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun invertebratesFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_invertebrates).perform(click())
        }
        invertebratesScreen {
            isDisplayed()
            fieldNotes().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun plantsFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_plants).perform(click())
        }
        plantsScreen {
            isDisplayed()
            fieldNotes().perform(scrollTo()).check(matches(isDisplayingAtLeast(90)))
        }
    }

    @Test
    fun threatsFormFieldsShouldBeVisible() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_threats).perform(click())
        }
        threatsScreen {
            isDisplayed()
            fieldThreatTypePoison(R.string.primary_type_poison).perform(scrollTo())
                .check(matches(isDisplayingAtLeast(90)))
        }
    }
}