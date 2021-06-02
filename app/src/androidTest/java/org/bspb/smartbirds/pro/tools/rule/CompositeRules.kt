package org.bspb.smartbirds.pro.tools.rule

import android.Manifest
import androidx.test.rule.GrantPermissionRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule

class CompositeRules {

    companion object {
        fun screenshotTestRule(): TestRule = RuleChain
            .outerRule(GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .around(ScreenshotTestRule())
    }
}