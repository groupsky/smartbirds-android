package org.bspb.smartbirds.pro

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import org.bspb.smartbirds.pro.tools.MockResponseHelper
import org.bspb.smartbirds.pro.tools.MonitoringHelper
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.finishMonitoring
import org.bspb.smartbirds.pro.tools.form.BirdsFormHelper
import org.bspb.smartbirds.pro.tools.form.BirdsFormHelper.Companion.fillFormFields
import org.bspb.smartbirds.pro.tools.robot.BirdsFormTestRobot.Companion.birdsScreen
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.MultipleChoiceDialogTestRobot
import org.bspb.smartbirds.pro.tools.robot.MultipleChoiceDialogTestRobot.Companion.multipleChoiceDialog
import org.bspb.smartbirds.pro.tools.rule.ActiveMonitoringRule
import org.bspb.smartbirds.pro.tools.rule.CompositeRules
import org.bspb.smartbirds.pro.tools.rule.DbRule
import org.bspb.smartbirds.pro.tools.rule.MockBackendRule
import org.bspb.smartbirds.pro.tools.selectMultipleChoice
import org.bspb.smartbirds.pro.utils.debugLog
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.gson.reflect.TypeToken

import com.google.gson.Gson
import org.bspb.smartbirds.pro.tools.MonitoringHelper.Companion.uploadData
import org.bspb.smartbirds.pro.tools.form.entry.builder.BirdEntryBuilder
import org.bspb.smartbirds.pro.tools.hasFormEntry
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot.Companion.mainScreen
import org.hamcrest.MatcherAssert.assertThat


@RunWith(AndroidJUnit4::class)
class BirdsFormTest {
    @Rule
    @JvmField
    var activeMonitoringRule = ActiveMonitoringRule()

    @Rule
    @JvmField
    var screenshotRule = CompositeRules.screenshotTestRule()

    @Rule
    @JvmField
    var dbRule = DbRule()

    var birdsEntry = BirdEntryBuilder()
        .setModeratorReview(false)
        .setConfidential(true)
        .setSpecies("Accipiter gentilis")
        .setCountUnit("Individuals")
        .setCountType("Exact number")
        .setCount("1")
        .build()
//    var birdsEntry: Map<Int, Any> = mapOf(
//        R.string.monitoring_moderator_review to false,
//        R.string.monitoring_birds_private to true,
//        R.string.monitoring_birds_name to "Accipiter gentilis",
//        R.string.monitoring_birds_count_unit to "Individuals",
//        R.string.monitoring_birds_count_type to "Exact number",
//        R.string.monitoring_birds_count to "1",
//        R.string.monitoring_birds_min to "2",
//        R.string.monitoring_birds_max to "3",
//        R.string.monitoring_birds_status to "Singing male",
//        R.string.monitoring_birds_behaviour to arrayOf("Feeding"),
//        R.string.monitoring_birds_gender to "Male",
//        R.string.monitoring_birds_age to "Pull",
//        R.string.monitoring_birds_nesting to "Nests",
//        R.string.monitoring_birds_death to "Poison",
//        R.string.monitoring_birds_marking to "Color ring",
//        R.string.monitoring_birds_substrate to "On bushes",
//        R.string.monitoring_birds_tree to "Tree",
//        R.string.monitoring_birds_tree_height to "50",
//        R.string.monitoring_birds_tree_location to "Single tree",
//        R.string.monitoring_birds_nest_height to "1-3 m.",
//        R.string.monitoring_birds_nest_location to "Next to trunk",
//        R.string.monitoring_birds_incubation to true,
//        R.string.monitoring_birds_eggs_count to "3",
//        R.string.monitoring_birds_small_downy_count to "1",
//        R.string.monitoring_birds_small_feathered_count to "5",
//        R.string.monitoring_birds_takeoff_count to "7",
//        R.string.monitoring_birds_nest_guard to true,
//        R.string.monitoring_birds_female_age to "6 years",
//        R.string.monitoring_birds_male_age to "5 years",
//        R.string.monitoring_birds_nest_success to "Occupied nest",
//        R.string.monitoring_birds_land_use to "garden",
//        R.string.monitoring_common_threats to arrayOf("Solar park"),
//        R.string.monitoring_birds_notes to "some notes",
//    )

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
        debugLog("JSON: $uploadRequestJson")
        assertThat(uploadRequestJson, hasFormEntry(birdsEntry))
    }
}