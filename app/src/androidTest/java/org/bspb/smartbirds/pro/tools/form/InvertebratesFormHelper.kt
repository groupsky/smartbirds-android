package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.InvertebratesFormTestRobot.Companion.invertebratesScreen
import org.bspb.smartbirds.pro.tools.selectMultipleChoice
import org.bspb.smartbirds.pro.tools.selectSingleChoice
import org.bspb.smartbirds.pro.tools.selectSpecies

class InvertebratesFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            invertebratesScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_invertebrates_private -> {
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_herp_name -> {
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_herp_gender -> {
                        selectSingleChoice(fieldGender(), value as String)
                    }
                    R.string.monitoring_herp_age -> {
                        selectSingleChoice(fieldAge(), value as String)
                    }
                    R.string.monitoring_herp_count -> {
                        fillTextField(fieldCount(), value as String)
                    }
                    R.string.monitoring_herp_habitat -> {
                        selectSingleChoice(fieldHabitat(), value as String)
                    }
                    R.string.monitoring_invertebrates_danger_observation -> {
                        selectMultipleChoice(
                            fieldFindings(),
                            (value as List<String>).toTypedArray()
                        )
                    }
                    R.string.monitoring_herp_marking -> {
                        fillTextField(fieldMarking(), value as String)
                    }
                    R.string.monitoring_common_threats -> {
                        selectMultipleChoice(fieldThreats(), (value as List<String>).toTypedArray())
                    }
                    R.string.monitoring_herp_notes -> {
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}