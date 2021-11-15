package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.*
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.PlantsFormTestRobot.Companion.plantsScreen
import org.bspb.smartbirds.pro.tools.robot.ThreatsFormTestRobot.Companion.threatsScreen
import org.bspb.smartbirds.pro.ui.utils.FormsConfig
import org.bspb.smartbirds.pro.ui.utils.FormsConfig.NomenclatureConfig

class ThreatsFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            threatsScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        tabMain().perform(click())
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_private -> {
                        tabMain().perform(click())
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_threats_primary_type -> {
                        tabMain().perform(click())
                        fieldThreatTypePoison((value as NomenclatureConfig).labelId).perform(
                            scrollTo(),
                            click()
                        )
                    }
                    R.string.monitoring_threats_category -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldCategory(), value as String)
                    }
                    R.string.monitoring_threats_class -> {
                        tabMain().perform(click())
                        selectSingleChoiceConfig(fieldClass(), value as String)
                    }
                    R.string.monitoring_threats_species -> {
                        tabMain().perform(click())
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_threats_count -> {
                        tabMain().perform(click())
                        fillTextField(fieldCount(), value as String)
                    }
                    R.string.monitoring_threats_estimate -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldEstimate(), value as String)
                    }
                    R.string.monitoring_threats_poisoned_type -> {
                        tabMain().perform(click())
                        fieldPoisoning((value as NomenclatureConfig).labelId).perform(
                            scrollTo(),
                            click()
                        )
                    }
                    R.string.monitoring_threats_state_carcass -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldStateCarcass(), value as String)
                    }
                    R.string.monitoring_threats_sample_taken_1 -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldSampleTaken1(), value as String)
                    }
                    R.string.monitoring_threats_sample_taken_2 -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldSampleTaken2(), value as String)
                    }
                    R.string.monitoring_threats_sample_taken_3 -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldSampleTaken3(), value as String)
                    }
                    R.string.monitoring_threats_sample_code_1 -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSampleCode1(), value as String)
                    }
                    R.string.monitoring_threats_sample_code_2 -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSampleCode2(), value as String)
                    }
                    R.string.monitoring_threats_sample_code_3 -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSampleCode3(), value as String)
                    }
                    R.string.monitoring_threats_notes -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}