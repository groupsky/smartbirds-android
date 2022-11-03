package org.bspb.smartbirds.pro.tools.rule

import android.Manifest
import androidx.test.rule.GrantPermissionRule
import org.bspb.smartbirds.pro.tools.rule.internal.BatteryNotification
import org.bspb.smartbirds.pro.tools.rule.internal.LoggedIn
import org.bspb.smartbirds.pro.tools.rule.internal.VersionCodeCheck
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SmartbirdsStateRule(val statement: Statement) : TestRule {

    companion object {
        fun setLoggedIn(state: Boolean) = SmartbirdsStateRule(LoggedIn(state))

        fun setBatteryNotification(shown: Boolean) = SmartbirdsStateRule(BatteryNotification(shown))

        fun setVersionCheck(check: Boolean) = SmartbirdsStateRule(VersionCodeCheck(check))

        fun grantMonitoringPermissions(): GrantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    override fun apply(base: Statement?, description: Description?) = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                statement.evaluate()

                base?.evaluate()
            }
        }
    }
}