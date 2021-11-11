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
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.tools.rule.MockBackendRule
import org.bspb.smartbirds.pro.tools.rule.MockLocationRule
import org.bspb.smartbirds.pro.tools.rule.SmartbirdsStateRule
import org.bspb.smartbirds.pro.ui.MainActivity_
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Before
    fun setUp() {
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
            Thread.sleep(1000)
            fieldObservationMethodology().perform(click())
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
            locationRule.updateLocation()

            // Wait before pressing the button otherwise the test is failing sometimes on the CI
            Thread.sleep(500)

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
            // Fill observation methodology
            fieldObservationMethodology().perform(click())
            singleChoiceDialog {
                listItem(0).perform(click())
            }

            buttonFinish().perform(click())
        }

        // Upload record
        // mock sync responses. We need to enqueue all the response, so the sync dialog can disappear.
        // Otherwise the test freezes.
        val nomenclaturesResponse = MockResponseHelper.prepareNomenclatureResponse()
        mockApiRule.server.enqueue(MockResponseHelper.prepareUploadFileResponse())
        mockApiRule.server.enqueue(MockResponseHelper.prepareUploadFormRespons())
        mockApiRule.server.enqueue(MockResponseHelper.prepareSuccessLoginResponse())
        mockApiRule.server.enqueue(nomenclaturesResponse)
        mockApiRule.server.enqueue(nomenclaturesResponse)

        mainScreen {
            buttonSync().perform(click())
        }
        mockApiRule.server.takeRequest()
        // Assert request
        val uploadBirdsRequest = mockApiRule.server.takeRequest().requestUrl.toString()
        assertThat(uploadBirdsRequest, endsWith("/birds"))
    }
}