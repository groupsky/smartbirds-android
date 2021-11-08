package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.HerptilesFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.HerptileEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.HerptilesFormTestRobot.Companion.herptilesScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HerptilesFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var herptilesEntry = HerptileEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Lacerta viridis")
        .setGender("F (Female)")
        .setAge("Juv")
        .setCount("2")
        .setHabitat("2. Mixed forests")
        .setFindings(listOf("Feces", "Hole"))
        .setMarking("some marking")
        .setDistanceFromAxis("5")
        .setThreats(listOf("Fires", "Hunting"))
        .setNotes("some notes")
        .setScl("55.6")
        .setMpl("33.5")
        .setMcw("12.3")
        .setHLcap("66.5")
        .setWeight("120.6")
        .setTempSubstrate("23.4")
        .setTempAir("12.34")
        .setTempCloaca("10")
        .setSqVentr("20")
        .setSqCaud("30")
        .setSqDors("40")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_herptile)
        }

        fillFormFields(herptilesEntry)

        herptilesScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(herptilesEntry))
    }
}