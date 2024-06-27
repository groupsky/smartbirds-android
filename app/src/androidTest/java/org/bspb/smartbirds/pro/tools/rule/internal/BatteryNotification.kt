package org.bspb.smartbirds.pro.tools.rule.internal

import androidx.test.core.app.ApplicationProvider
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs
import org.junit.runners.model.Statement

class BatteryNotification(private val shown: Boolean) : Statement() {

    override fun evaluate() {
        with(SmartBirdsPrefs(ApplicationProvider.getApplicationContext())) {
            setBatteryOptimizationDialogShown(shown)
        }
    }

}