package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.PlantsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.PlantsEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.PlantsFormTestRobot.Companion.plantsScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PlantsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var plantEntry = PlantsEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Achillea lingulata")
        .setReportingUnit("Bush")
        .setPhenologicalPhase("Complete opening of buds")
        .setCount("2")
        .setDensity("22.3")
        .setCover("33.4")
        .setHabitat("Fen")
        .setThreatsPlants(listOf("Fires", "Logging"))
        .setThreats(listOf("Fires", "Hunting"))
        .setElevation("350")
        .setNotes("some notes")
        .setAccompanyingSpecies(listOf("Artemisia eriantha*", "Asplenium cuneifolium*"))
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_plants)
        }

        fillFormFields(plantEntry)

        plantsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(plantEntry))
    }
}