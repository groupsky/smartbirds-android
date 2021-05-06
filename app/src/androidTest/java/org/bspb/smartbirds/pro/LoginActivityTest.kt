package org.bspb.smartbirds.pro

import android.Manifest.permission.READ_CONTACTS
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import okhttp3.mockwebserver.MockResponse
import org.bspb.smartbirds.pro.androidTest.AlertDialogTestRobot.Companion.alertDialog
import org.bspb.smartbirds.pro.androidTest.LoginTestRobot.Companion.loginScreen
import org.bspb.smartbirds.pro.androidTest.MainTestRobot.Companion.mainScreen
import org.bspb.smartbirds.pro.androidTest.MockBackendTestRule
import org.bspb.smartbirds.pro.backend.dto.*
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.LoginActivity_
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.ArrayList
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(LoginActivity_::class.java)

    @Rule
    @JvmField
    val mockApiRule = MockBackendTestRule()

    @Rule
    @JvmField
    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(
        READ_CONTACTS
    )

    @Test
    fun testLoginSuccessFlow() {
        var parser = SBGsonParser.createParser()
        var loginResponse = LoginResponse()
        loginResponse.success = true
        loginResponse.token = "token"
        loginResponse.user = User()
        var mockLoginResponse = MockResponse().setBody(parser.toJson(loginResponse))
        var nomenclaturesResponse = ResponseListEnvelope<Nomenclature>()
        nomenclaturesResponse.count = 0
        nomenclaturesResponse.data = ArrayList()
        // login
        mockApiRule.server.enqueue(mockLoginResponse)
        // check session
        mockApiRule.server.enqueue(mockLoginResponse)
        // nomenclatures
        mockApiRule.server.enqueue(MockResponse().setBody(parser.toJson(nomenclaturesResponse)))
        // species
//        mockApiRule.server.enqueue(MockResponse().setBody(parser.toJson(nomenclaturesResponse)))
        // locations
        mockApiRule.server.enqueue(MockResponse().setBody(parser.toJson(nomenclaturesResponse)))
        // zones
        mockApiRule.server.enqueue(MockResponse().setBody(parser.toJson(nomenclaturesResponse)))

        loginScreen {
            isDisplayed()
            usernameInput().perform(typeText("user@smartbirds.org"))
            passwordInput().perform(typeText("Secret1!"))
            loginButton().perform(scrollTo(), click())
        }
        assertEquals("/session", mockApiRule.server.takeRequest().path)
        assertEquals("/session", mockApiRule.server.takeRequest().path)
        assertEquals("/nomenclature?limit=500&offset=0", mockApiRule.server.takeRequest().path)
//        assertEquals("/species?limit=500&offset=0", mockApiRule.server.takeRequest().path)
        assertEquals("/locations?limit=-1", mockApiRule.server.takeRequest().path)
        assertEquals("/zone?limit=-1&status=owned", mockApiRule.server.takeRequest().path)

        alertDialog {
            isDisplayed(R.string.battery_optimization_title)
            button1().perform(scrollTo(), click())
        }

        mainScreen {
            isDisplayed()
        }
    }
}