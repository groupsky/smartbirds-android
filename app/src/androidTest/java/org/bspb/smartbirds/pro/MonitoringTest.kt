package org.bspb.smartbirds.pro

import android.app.Activity
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MockResponseHelper
import org.bspb.smartbirds.pro.tools.nomenclatureWithLabel
import org.bspb.smartbirds.pro.tools.robot.BirdsFormTestRobot.Companion.birdsScreen
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot.Companion.mainScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.tools.rule.MockBackendRule
import org.bspb.smartbirds.pro.tools.rule.MockLocationRule
import org.bspb.smartbirds.pro.tools.rule.SmartbirdsStateRule
import org.bspb.smartbirds.pro.ui.MainActivity_
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.endsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MonitoringTest {

    // Must be applied after all other rules
    @Rule(order = 1)
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity_::class.java)

    @Rule
    @JvmField
    val mockApiRule = MockBackendRule()

    @Rule
    @JvmField
    val permissionsRule = SmartbirdsStateRule.grantMonitoringPermissions()

    @Rule
    @JvmField
    val loggedInRule = SmartbirdsStateRule.setLoggedIn(true)

    @Rule
    @JvmField
    val batteryNotificationRule = SmartbirdsStateRule.setBatteryNotification(true)

    @Rule
    @JvmField
    val locationRule = MockLocationRule()

    @Before
    fun prepareTests() {
        var activity: Activity? = null
        activityRule.scenario.onActivity {
            activity = it
        }
        locationRule.initFusedProvider(activity)
    }


    @Test
    fun testSuccessMonitoring() {
        // Start monitoring from main screen
        mainScreen {
            isDisplayed()
            buttonStart().perform(click())
        }

        // Fill source field in common form and submit it
        commonFormScreen {
            isDisplayed()
            // Wait a bit for the view to load the data
            Thread.sleep(500)
            fieldSource().perform(click())
        }
        singleChoiceDialog {
            listItem(0).perform(click())
        }
        commonFormScreen {
            buttonSubmit().perform(click())
        }


        // Open birds form
        monitoringScreen {
            isDisplayed()
            Thread.sleep(250)
            locationRule.updateLocation()

            // Wait
            Thread.sleep(1000)
            buttonFabAddEntry().perform(click())

            onData(
                `is`(
                    ApplicationProvider.getApplicationContext<Context>()
                        .getString(R.string.entry_type_birds)
                )
            ).perform(click())
        }

        // Fill required fields
        birdsScreen {
            isDisplayed()

            // select species
            fieldSpecies().perform(click())
            singleChoiceDialog {
                listItem(0).perform(click())
            }

            // select count unit
            fieldCountUnit().perform(click())
            singleChoiceDialog {
                listItem(0).perform(click())
            }

            // select count type
            fieldCountType().perform(click())
            singleChoiceDialog {
                onData(nomenclatureWithLabel("Exact number")).perform(click())
            }

            // enter count
            fieldCount().perform(typeText("1"))

            buttonSave().perform(click())
        }

        // Finish monitoring
        monitoringScreen {
            buttonFinish().perform(click())
        }
        commonFormScreen {
            buttonFinish().perform(click())
        }

        // Upload record
        // mock gpx upload response
        mockApiRule.server.enqueue(MockResponseHelper.prepareUploadFileResponse())
        mainScreen {
            buttonSync().perform(click())
        }
        mockApiRule.server.takeRequest()

        // Assert request
        val uploadBirdsRequest = mockApiRule.server.takeRequest().requestUrl.toString()
        assertThat(uploadBirdsRequest, endsWith("/birds"))
    }
}