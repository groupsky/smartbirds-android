package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.PylonsFormHelper
import org.bspb.smartbirds.pro.tools.form.PylonsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.PylonsEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.PylonsFormTestRobot
import org.bspb.smartbirds.pro.tools.robot.PylonsFormTestRobot.Companion.pylonsScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PylonsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var pylonEntry = PylonsEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setPylonType("Type 2")
        .setSpeciesNestOnPylon("Accipiter gentilis")
        .setNestType("Natural nest")
        .setPylonInsulated(true)
        .setPrimaryHabitat("Grassland")
        .setSecondaryHabitat("Arable land")
        .setNotes("some notes")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_pylons)
        }

        fillFormFields(pylonEntry)

        pylonsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(pylonEntry))
    }
}