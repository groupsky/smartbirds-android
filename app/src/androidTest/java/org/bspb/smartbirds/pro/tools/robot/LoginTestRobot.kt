package org.bspb.smartbirds.pro.tools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.robot.TestRobotMarker

@TestRobotMarker
private interface LoginRobot

class LoginTestRobot : LoginRobot {
    companion object {

        fun loginScreen(block: LoginTestRobot.() -> Unit): LoginTestRobot {
            return LoginTestRobot().apply(block)
        }
    }

    fun isDisplayed(): ViewInteraction =
        toolbarWithTitle(R.string.title_activity_login).check(matches(ViewMatchers.isDisplayed()))

    fun fieldUsername(): ViewInteraction = onView(withHint(R.string.prompt_email))
    fun fieldPassword(): ViewInteraction = onView(withHint(R.string.prompt_password))
    fun gdprCheck(): ViewInteraction = onView(withText(R.string.gdpr_agree_checkbox))
    fun buttonLogin(): ViewInteraction = onView(withId(R.id.email_sign_in_button))
}