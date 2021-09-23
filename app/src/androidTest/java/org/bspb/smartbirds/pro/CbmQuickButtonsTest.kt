package org.bspb.smartbirds.pro

import android.content.Context
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase
import org.bspb.smartbirds.pro.tools.robot.CbmFormTestRobot.Companion.cbmScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.tools.viewactions.CustomScrollAction
import org.bspb.smartbirds.pro.tools.viewactions.ScrollActions
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
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

    @Test
    fun testDistanceValuesAreVisible() {
        cbmScreen {
            Espresso.onView(withText("3 - (over 100 m)"))
                .check(matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun testDistanceIsProperlyStored() {
        cbmScreen {
            fillRequiredFields()

            // set distance field
            Espresso.onView(withText("3 - (over 100 m)")).perform(click())

            buttonSave().perform(click())

            var db = SmartBirdsDatabase.getInstance(ApplicationProvider.getApplicationContext())
            assertThat(
                db.readableDatabase.rawQuery("SELECT * FROM forms", null).count,
                Matchers.`is`(1)
            )
        }
    }

}