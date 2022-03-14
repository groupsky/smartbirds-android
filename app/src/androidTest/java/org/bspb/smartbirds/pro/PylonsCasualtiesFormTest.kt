package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.PylonsCasualtiesFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.form.entry.builder.PylonsCasualtiesEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.PylonsCasualtiesFormTestRobot.Companion.pylonsCasualtiesScreen
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PylonsCasualtiesFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    private var pylonCasualtyEntry = PylonsCasualtiesEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setCauseOfDeath("Collision")
        .setSpecies("Accipiter gentilis")
        .setCount("1")
        .setAge("Pull")
        .setGender("Male")
        .setBodyCondition("Fresh corpse")
        .setPrimaryHabitat("Grassland")
        .setSecondaryHabitat("Arable land")
        .setNotes("some notes")
        .build()

    @Test
    fun submitEntryToServer() {
        monitoringScreen {
            openNewEntryForm(R.string.entry_type_pylons_casualties)
        }

        fillFormFields(pylonCasualtyEntry)

        pylonsCasualtiesScreen {
            buttonSave().perform(click())
        }

        finishMonitoring()

        uploadData(activeMonitoringRule.mockApiRule.server)

        val uploadRequestJson =
            activeMonitoringRule.mockApiRule.server.takeRequest().body.readUtf8()

        assertThat(uploadRequestJson, hasFormEntry(pylonCasualtyEntry))
    }
}