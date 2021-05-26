package org.bspb.smartbirds.pro.tools.rule.internal

import androidx.test.core.app.ApplicationProvider
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.junit.runners.model.Statement

class BatteryNotification(private val shown: Boolean) : Statement() {

    override fun evaluate() {
        SmartBirdsPrefs_(ApplicationProvider.getApplicationContext())
            .edit()
            .isBatteryOptimizationDialogShown.put(shown)
            .apply()
    }

}