package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.ThreatsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.ThreatsEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.ThreatsFormTestRobot
import org.bspb.smartbirds.pro.tools.robot.ThreatsFormTestRobot.Companion.threatsScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.ui.utils.FormsConfig
import org.bspb.smartbirds.pro.ui.utils.FormsConfig.ThreatsPoisonedType.dead
import org.bspb.smartbirds.pro.ui.utils.FormsConfig.ThreatsPrimaryType.poison
import org.bspb.smartbirds.pro.ui.utils.FormsConfig.ThreatsPrimaryType.threat
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ThreatsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var threatEntry = ThreatsEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setPrimaryType(threat)
        .setCategory("Fires")
        .setClass("mammals")
        .setSpecies("Dama dama")
        .setCount("2")
        .setEstimate("High")
        .setNotes("some notes")
        .build()

    private var poisonedEntry = ThreatsEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setPrimaryType(poison)
        .setPoisonedType(dead)
        .setClass("mammals")
        .setSpecies("Dama dama")
        .setCount("2")
        .setStateCarcass("Fresh")
        .setSampleTaken1("Heart")
        .setSampleCode1("sample code 1")
        .setSampleTaken2("Liver")
        .setSampleCode2("sample code 2")
        .setSampleTaken3("Brain")
        .setSampleCode3("sample code 3")
        .setNotes("some notes")
        .build()

    @Test
    fun submitThreatEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_threats)
        }

        fillFormFields(threatEntry)

        threatsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(threatEntry))
    }

    @Test
    fun submitPoisonEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_threats)
        }

        fillFormFields(poisonedEntry)

        threatsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(poisonedEntry))
    }
}