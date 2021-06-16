package org.bspb.smartbirds.pro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.ui.SplashScreenActivity_
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<SplashScreenActivity_> =
        ActivityScenarioRule(SplashScreenActivity_::class.java)

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Test
    fun testSplash() {
        onView(ViewMatchers.withText("SmartBirds Pro")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}