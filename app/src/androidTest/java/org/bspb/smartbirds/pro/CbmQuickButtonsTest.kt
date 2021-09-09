package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.robot.CbmFormTestRobot.Companion.cbmScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.Matchers.startsWithIgnoringCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CbmQuickButtonsTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Before
    fun setUp() {
        monitoringScreen {
            buttonFabAddEntry().perform(click())

            monitoringType(R.string.entry_type_cbm).perform(click())
        }

        cbmScreen {
            isDisplayed()
        }
    }

    @Test
    fun testQuickButtonValueIsAssigned() {
        // Click on species quick button to set a value
        cbmScreen {
            quickChoiceButtons()[1].perform(click())
        }

        // Choose a value from the list
        singleChoiceDialog {
            onSpeciesRow("Accipiter gentilis").perform(click())
        }

        cbmScreen {
            // Check that the quick button value is properly assigned
            // "acc gen" stands for "Accipiter gentilis" which was the selected species in previous step
            quickChoiceButtons()[1].check(matches(withText(startsWithIgnoringCase("acc gen"))))
        }
    }

    @Test
    fun testQuickButtonValueIsReassigned() {
        // Click on species quick button to set a value
        cbmScreen {
            quickChoiceButtons()[1].perform(click())
        }

        // Choose a value from the list
        singleChoiceDialog {
            onSpeciesRow("Accipiter gentilis").perform(click())
        }

        cbmScreen {
            // Check that the quick button value is properly assigned
            // "acc gen" stands for "Accipiter gentilis" which was the selected species in previous step
            quickChoiceButtons()[1].check(matches(withText(startsWithIgnoringCase("acc gen"))))
            // Long click on species quick button to reassign the value
            quickChoiceButtons()[1].perform(longClick())
        }

        // Choose a value from the list
        singleChoiceDialog {
            onSpeciesRow("Accipiter nisus").perform(click())
        }

        cbmScreen {
            // Check that the quick button value is properly assigned
            quickChoiceButtons()[1].check(matches(withText(startsWithIgnoringCase("acc nis"))))
        }
    }

    @Test
    fun testSpeciesValueIsAssigned() {
        // Click on species quick button to set a value
        cbmScreen {
            quickChoiceButtons()[1].perform(click())
        }

        // Choose a value from the list
        singleChoiceDialog {
            // select "Accipiter gentilis" from the list
            onSpeciesRow("Accipiter gentilis").perform(click())
        }

        cbmScreen {
            quickChoiceButtons()[1].perform(click())
        }

        cbmScreen {
            fieldSpecies().check(matches(withText(startsWithIgnoringCase("Accipiter gentilis"))))
        }

    }
}