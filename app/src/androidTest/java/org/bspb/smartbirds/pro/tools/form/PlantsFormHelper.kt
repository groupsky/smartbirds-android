package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.*
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.PlantsFormTestRobot.Companion.plantsScreen

class PlantsFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            plantsScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        tabMain().perform(click())
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_plants_private -> {
                        tabMain().perform(click())
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_plants_name -> {
                        tabMain().perform(click())
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_plants_reporting_unit -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldReportingUnit(), value as String)
                    }
                    R.string.monitoring_plants_phenological_phase -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldPhenologicalPhase(), value as String)
                    }
                    R.string.monitoring_plants_count -> {
                        tabMain().perform(click())
                        fillTextField(fieldCount(), value as String)
                    }
                    R.string.monitoring_plants_density -> {
                        tabMain().perform(click())
                        fillTextField(fieldDensity(), value as String)
                    }
                    R.string.monitoring_plants_cover -> {
                        tabMain().perform(click())
                        fillTextField(fieldCover(), value as String)
                    }
                    R.string.monitoring_plants_habitat -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldHabitat(), value as String)
                    }
                    R.string.monitoring_plants_threats -> {
                        tabMain().perform(click())
                        selectMultipleChoice(
                            fieldThreatsAndImpacts(),
                            (value as List<String>).toTypedArray()
                        )
                    }
                    R.string.monitoring_common_threats -> {
                        tabMain().perform(click())
                        selectMultipleChoice(fieldThreats(), (value as List<String>).toTypedArray())
                    }
                    R.string.monitoring_plants_elevation -> {
                        tabMain().perform(click())
                        fillTextField(fieldElevation(), value as String)
                    }
                    R.string.monitoring_plants_notes -> {
                        tabMain().perform(click())
                        fillTextField(fieldNotes(), value as String)
                    }
                    R.string.monitoring_plants_accompanying_species -> {
                        Thread.sleep(1000)
                        tabOptional().perform(click())
                        Thread.sleep(1000)
                        selectMultipleSpeciesFullScreen(
                            R.string.monitoring_plants_accompanying_species,
                            (value as List<String>).toTypedArray()
                        )
                    }
                }
            }

        }
    }
}