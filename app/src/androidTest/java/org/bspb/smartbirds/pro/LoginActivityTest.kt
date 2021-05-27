package org.bspb.smartbirds.pro

import android.Manifest.permission.READ_CONTACTS
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.bspb.smartbirds.pro.tools.MockResponseHelper.Companion.prepareNomenclatureResponse
import org.bspb.smartbirds.pro.tools.MockResponseHelper.Companion.prepareSuccessLoginResponse
import org.bspb.smartbirds.pro.tools.robot.AlertDialogTestRobot.Companion.alertDialog
import org.bspb.smartbirds.pro.tools.robot.LoginTestRobot.Companion.loginScreen
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot.Companion.mainScreen
import org.bspb.smartbirds.pro.tools.rule.MockBackendRule
import org.bspb.smartbirds.pro.tools.rule.SmartbirdsStateRule
import org.bspb.smartbirds.pro.ui.LoginActivity_
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    val activityRule: ActivityScenarioRule<LoginActivity_> =
        ActivityScenarioRule(LoginActivity_::class.java)

    @Rule
    @JvmField
    val mockApiRule = MockBackendRule()

    @Rule
    @JvmField
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(READ_CONTACTS)

    @Rule
    @JvmField
    val batteryNotificationRule = SmartbirdsStateRule.setBatteryNotification(true)

    @Test
    fun testLoginSuccess() {
        val loginResponse = prepareSuccessLoginResponse()
        val nomenclaturesResponse = prepareNomenclatureResponse()

        // login
        mockApiRule.server.enqueue(loginResponse)
        // check session
        mockApiRule.server.enqueue(loginResponse)
        // nomenclatures
        mockApiRule.server.enqueue(nomenclaturesResponse)
        // zones
        mockApiRule.server.enqueue(nomenclaturesResponse)

        loginScreen {
            isDisplayed()
            fieldUsername().perform(typeText("user@smartbirds.org"))
            fieldPassword().perform(typeText("Secret1!"), closeSoftKeyboard())
            buttonLogin().perform(scrollTo(), click())
        }

        mainScreen {
            isDisplayed()
        }
    }
}