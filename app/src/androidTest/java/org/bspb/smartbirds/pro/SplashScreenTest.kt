package org.bspb.smartbirds.pro

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.bspb.smartbirds.pro.tools.rule.ScreenshotTestRule
import org.bspb.smartbirds.pro.ui.SplashScreenActivity_
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<SplashScreenActivity_> =
        ActivityScenarioRule(SplashScreenActivity_::class.java)

    @Rule
    @JvmField
    var screenshotRule = RuleChain
            .outerRule(GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .around(ScreenshotTestRule())

    @Test
    fun testSplash() {
        onView(ViewMatchers.withText("SmartBirds Pro")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}