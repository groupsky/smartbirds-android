package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.CbmFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.CbmEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.CbmFormTestRobot.Companion.cbmScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CbmFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var cbmEntry = CbmEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Accipiter gentilis")
        .setCount("1")
        .setDistance("1 - (0 - 25 m)")
        .setZone("TEST123")
        .setThreats(listOf("Solar park"))
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_cbm)
        }

        fillFormFields(cbmEntry)

        cbmScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()
        assertThat(uploadRequestJson, hasFormEntry(cbmEntry))
    }
}