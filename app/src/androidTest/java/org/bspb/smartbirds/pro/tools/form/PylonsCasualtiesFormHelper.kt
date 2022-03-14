package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.PylonsCasualtiesFormTestRobot.Companion.pylonsCasualtiesScreen
import org.bspb.smartbirds.pro.tools.selectSingleChoice
import org.bspb.smartbirds.pro.tools.selectSpecies

class PylonsCasualtiesFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            pylonsCasualtiesScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_pylons_casualties_private -> {
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_pylons_casualties_name -> {
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_birds_count -> {
                        fillTextField(fieldCount(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_age -> {
                        selectSingleChoice(fieldAge(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_gender -> {
                        selectSingleChoice(fieldGender(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_cause_of_death -> {
                        selectSingleChoice(fieldCauseOfDeath(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_body_condition -> {
                        selectSingleChoice(fieldBodyCondition(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_primary_habitat -> {
                        selectSingleChoice(fieldPrimaryHabitat(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_secondary_habitat -> {
                        selectSingleChoice(fieldSecondaryHabitat(), value as String)
                    }
                    R.string.monitoring_pylons_casualties_notes -> {
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}