package org.bspb.smartbirds.pro.tools.rule.internal

import androidx.test.core.app.ApplicationProvider
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs
import org.junit.runners.model.Statement

class VersionCodeCheck(private val check: Boolean) : Statement() {

    override fun evaluate() {
        if (!check) {
            with(SmartBirdsPrefs(ApplicationProvider.getApplicationContext())) {
                setVersionCode(BuildConfig.VERSION_CODE)
            }
        }
    }

}