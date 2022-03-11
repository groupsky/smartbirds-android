package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.InvertebratesFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.InvertebratesEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.InvertebratesFormTestRobot.Companion.invertebratesScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class InvertebratesFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var invertebratesEntry = InvertebratesEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Arytrura musculus")
        .setGender("F (Female)")
        .setAge("Juv")
        .setCount("2")
        .setHabitat("2. Mixed forests")
        .setFindings(listOf("Feces", "Hole"))
        .setMarking("some marking")
        .setThreats(listOf("Fires", "Hunting"))
        .setNotes("some notes")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_invertebrates)
        }

        fillFormFields(invertebratesEntry)

        invertebratesScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(invertebratesEntry))
    }
}