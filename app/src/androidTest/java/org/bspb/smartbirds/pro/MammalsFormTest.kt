package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.MammalsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.MammalEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MammalsFormTestRobot.Companion.mammalsScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MammalsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var mammalEntry = MammalEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Dama dama")
        .setGender("F (Female)")
        .setAge("Juv")
        .setCount("2")
        .setHabitat("2. Mixed forests")
        .setFindings(listOf("Feces", "Hole"))
        .setMarking("some marking")
        .setDistanceFromAxis("5")
        .setThreats(listOf("Fires", "Hunting"))
        .setNotes("some notes")
        .setL("11.1")
        .setC("22.2")
        .setA("33.3")
        .setPl("44.4")
        .setWeight("120.6")
        .setTempSubstrate("23.4")
        .setTempAir("12.34")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_mammal)
        }

        fillFormFields(mammalEntry)

        mammalsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(mammalEntry))
    }
}