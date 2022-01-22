package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.HerptilesFormTestRobot.Companion.herptilesScreen
import org.bspb.smartbirds.pro.tools.selectMultipleChoice
import org.bspb.smartbirds.pro.tools.selectSingleChoice
import org.bspb.smartbirds.pro.tools.selectSpecies

class HerptilesFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            herptilesScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        tabMain().perform(click())
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_herptiles_private -> {
                        tabMain().perform(click())
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_herp_name -> {
                        tabMain().perform(click())
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_herp_gender -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldGender(), value as String)
                    }
                    R.string.monitoring_herp_age -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldAge(), value as String)
                    }
                    R.string.monitoring_herp_count -> {
                        tabMain().perform(click())
                        fillTextField(fieldCount(), value as String)
                    }
                    R.string.monitoring_herp_habitat -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldHabitat(), value as String)
                    }
                    R.string.monitoring_herptiles_danger_observation -> {
                        tabMain().perform(click())
                        selectMultipleChoice(
                            fieldFindings(),
                            (value as List<String>).toTypedArray()
                        )
                    }
                    R.string.monitoring_herp_marking -> {
                        tabMain().perform(click())
                        fillTextField(fieldMarking(), value as String)
                    }
                    R.string.monitoring_herp_axis_distance -> {
                        tabMain().perform(click())
                        fillTextField(fieldDistanceFromAxis(), value as String)
                    }
                    R.string.monitoring_common_threats -> {
                        tabMain().perform(click())
                        selectMultipleChoice(fieldThreats(), (value as List<String>).toTypedArray())
                    }
                    R.string.monitoring_herp_notes -> {
                        tabMain().perform(click())
                        fillTextField(fieldNotes(), value as String)
                    }
                    R.string.monitoring_herp_scl -> {
                        tabOptional().perform(click())
                        fillTextField(fieldScl(), value as String)
                    }
                    R.string.monitoring_herp_mpl -> {
                        tabOptional().perform(click())
                        fillTextField(fieldMpl(), value as String)
                    }
                    R.string.monitoring_herp_mcw -> {
                        tabOptional().perform(click())
                        fillTextField(fieldMcw(), value as String)
                    }
                    R.string.monitoring_herp_h -> {
                        tabOptional().perform(click())
                        fillTextField(fieldHLcap(), value as String)
                    }
                    R.string.monitoring_herp_weight -> {
                        tabOptional().perform(click())
                        fillTextField(fieldWeight(), value as String)
                    }
                    R.string.monitoring_herp_t_substrate -> {
                        tabOptional().perform(click())
                        fillTextField(fieldTempSubstrate(), value as String)
                    }
                    R.string.monitoring_herp_t_vha -> {
                        tabOptional().perform(click())
                        fillTextField(fieldTempAir(), value as String)
                    }
                    R.string.monitoring_herp_t_kloaka -> {
                        tabOptional().perform(click())
                        fillTextField(fieldTempCloaca(), value as String)
                    }
                    R.string.monitoring_herp_sq_ventr -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSqVentr(), value as String)
                    }
                    R.string.monitoring_herp_sq_caud -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSqCaud(), value as String)
                    }
                    R.string.monitoring_herp_sq_dors -> {
                        tabOptional().perform(click())
                        fillTextField(fieldSqDors(), value as String)
                    }
                }
            }

        }
    }
}