package org.bspb.smartbirds.pro

import android.Manifest
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.bspb.smartbirds.pro.androidTest.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.androidTest.MainTestRobot
import org.bspb.smartbirds.pro.androidTest.MainTestRobot.Companion.mainScreen
import org.bspb.smartbirds.pro.androidTest.MockBackendTestRule
import org.bspb.smartbirds.pro.androidTest.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.androidTest.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.androidTest.SmartbirdsStateRule
import org.bspb.smartbirds.pro.ui.MainActivity_
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegressionsTest {

    // must be after all the other rules
    @Rule(order = 1)
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity_::class.java)

    @Rule
    @JvmField
    val mockApiRule = MockBackendTestRule()

    @Rule
    @JvmField
    val permissionsRule = SmartbirdsStateRule.grantMonitoringPermissions()

    @Rule
    @JvmField
    val loggedInRule = SmartbirdsStateRule.setLoggedIn(true)

    @Rule
    @JvmField
    val batteryNotificationRule = SmartbirdsStateRule.setBatteryNotification(true)

    @Test
    fun testBreakageAfterCancelMonitoring() {
        mainScreen {
            isDisplayed()
            startButton().perform(click())
        }
        commonFormScreen {
            isDisplayed()
            try {
                println("Clicking on source field")
                sourceField().perform(click())
            } catch (e: Throwable) {
                Thread.sleep(5000)
                throw e
            }
        }
        singleChoiceDialog {
            try {
                println("Selecting first item of single choice dialog")
                listItem(0).perform(click())
            } catch (e: Throwable) {
                Thread.sleep(5000)
                throw e
            }
        }
        commonFormScreen {
            try {
                println("Clicking submit button")
                submitButton().perform(click())
            } catch (e: Throwable) {
                Thread.sleep(5000)
                throw e
            }
        }
        monitoringScreen {
            isDisplayed()
            navigateUpButton().perform(click())
        }
        mainScreen {
            isDisplayed()
            cancelButton().perform(click())
            startButton().perform(click())
        }
        commonFormScreen {
            isDisplayed()
            sourceField().perform(click())
        }
        singleChoiceDialog {
            listItem(0).perform(click())
        }
        commonFormScreen {
            submitButton().perform(click())
        }
        // TODO check the current monitoring is not canceled
        Thread.sleep(5000)
    }
}