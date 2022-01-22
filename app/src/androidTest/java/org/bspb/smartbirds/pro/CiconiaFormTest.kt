package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.CiconiaFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.CiconiaEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.CiconiaFormTestRobot.Companion.ciconiaScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CiconiaFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var ciconiaEntry = CiconiaEntryBuilder()
        .setSubstrate("Tree")
        .setPylon("no powerlines")
        .setNestIsOnArtificialPlatform(true)
        .setPylonType("iron")
        .setTree("Dry")
        .setBuilding("chimney of bakery")
        .setNestIsOnHumanArtificialPlatform(true)
        .setNestOnAnotherSubstrate("another substrate")
        .setNestThisYearNotUtilizedByWhiteStorks("Appeared and then disappeared")
        .setThisYearOneTwoBirdsAppearedInNest("ONE bird")
        .setApproximateDateStorksAppeared("2021-11-05")
        .setApproximateDateDisappearanceWhiteStorks("2021-11-06")
        .setThisYearInTheNestAppeared("ONE bird")
        .setCountJuvenilesInNest("5")
        .setNestNotUsedForOverOneYear("3")
        .setDataOnJuvenileMortalityFromElectrocutions("3")
        .setDataOnJuvenilesExpelledFromParents("4")
        .setDiedOtherReasons("5")
        .setReason("some reason")
        .setThreats(listOf("Solar park"))
        .setNotes("Some notes")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_ciconia)
        }

        fillFormFields(ciconiaEntry)

        ciconiaScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()
        assertThat(uploadRequestJson, hasFormEntry(ciconiaEntry))
    }
}