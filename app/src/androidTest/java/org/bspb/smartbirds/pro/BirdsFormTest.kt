package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.BirdsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.BirdEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.BirdsFormTestRobot.Companion.birdsScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class BirdsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var birdsEntry = BirdEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Accipiter gentilis")
        .setCountUnit("Individuals")
        .setCountType("Exact number")
        .setCount("1")
        .setCountMin("2")
        .setCountMax("3")
        .setBirdsStatus("Singing male")
        .setBehaviour(listOf("Feeding"))
        .setGender("Male")
        .setAge("Pull")
        .setNesting("Nests")
        .setDeath("Poison")
        .setMarking("Color ring")
        .setSubstrate("On bushes")
        .setTree("Tree")
        .setTreeHeight("50")
        .setTreeLocation("Single tree")
        .setNestHeight("1-3 m.")
        .setNestLocation("Next to trunk")
        .setIncubation(true)
        .setEggsCount("3")
        .setSmallDownyCount("1")
        .setSmallFeatheredCount("5")
        .setTakeoffCount("7")
        .setNestGuard(true)
        .setFemaleAge("6 years")
        .setMaleAge("5 years")
        .setNestingSuccess("Occupied nest")
        .setLandUse("garden")
        .setThreats(listOf("Solar park"))
        .setNotes("some notes")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_birds)
        }

        fillFormFields(birdsEntry)

        birdsScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()
        assertThat(uploadRequestJson, hasFormEntry(birdsEntry))
    }
}