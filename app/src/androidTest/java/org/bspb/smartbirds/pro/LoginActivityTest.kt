package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MockResponseHelper.Companion.prepareMapLayersResponse
import org.bspb.smartbirds.pro.tools.MockResponseHelper.Companion.prepareNomenclatureResponse
import org.bspb.smartbirds.pro.tools.MockResponseHelper.Companion.prepareSuccessLoginResponse
import org.bspb.smartbirds.pro.tools.robot.LoginTestRobot.Companion.loginScreen
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot.Companion.mainScreen
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
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
    val batteryNotificationRule = SmartbirdsStateRule.setBatteryNotification(true)

    @Rule
    @JvmField
    val versionCheckRule = SmartbirdsStateRule.setVersionCheck(false)

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Test
    fun testLoginSuccess() {
        val loginResponse = prepareSuccessLoginResponse()
        val nomenclaturesResponse = prepareNomenclatureResponse()
        val mapLayersResponse = prepareMapLayersResponse()

        // login
        mockApiRule.server.enqueue(loginResponse)
        // check session
        mockApiRule.server.enqueue(loginResponse)
        // nomenclatures
        mockApiRule.server.enqueue(nomenclaturesResponse)
        // zones
        mockApiRule.server.enqueue(nomenclaturesResponse)
        // pois
        mockApiRule.server.enqueue(nomenclaturesResponse)
        // app settings
        mockApiRule.server.enqueue(mapLayersResponse)

        loginScreen {
            isDisplayed()
            fieldUsername().perform(typeText("user@smartbirds.org"))
            fieldPassword().perform(typeText("Secret1!"), closeSoftKeyboard())
            buttonLogin().perform(scrollTo(), click())
        }

        mainScreen {
            // Wait for main screen to be shown
            Thread.sleep(1000)

            isDisplayed()
        }
    }
}