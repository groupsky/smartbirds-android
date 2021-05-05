package org.bspb.smartbirds.pro.androidTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.bspb.smartbirds.pro.R

@TestRobotMarker
private interface LoginRobot

class LoginTestRobot : LoginRobot {

    companion object {

        fun loginScreen(block: LoginTestRobot.() -> Unit): LoginTestRobot {
            return LoginTestRobot().apply(block)
        }
    }

    fun isDisplayed() =
        toolbarWithTitle(R.string.title_activity_login).check(matches(ViewMatchers.isDisplayed()))

    fun usernameInput() = onView(withHint(R.string.prompt_email))
    fun passwordInput() = onView(withHint(R.string.prompt_password))
    fun gdprCheck() = onView(withText(R.string.gdpr_agree_checkbox))
    fun loginButton() = onView(withId(R.id.email_sign_in_button))
}
