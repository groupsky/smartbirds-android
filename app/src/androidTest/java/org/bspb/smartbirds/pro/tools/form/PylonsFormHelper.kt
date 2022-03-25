package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.PylonsFormTestRobot.Companion.pylonsScreen
import org.bspb.smartbirds.pro.tools.selectSingleChoice
import org.bspb.smartbirds.pro.tools.selectSpecies

class PylonsFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            pylonsScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_private -> {
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_pylons_pylon_type -> {
                        selectSingleChoice(fieldPylonType(), value as String)
                    }
                    R.string.monitoring_pylons_species_nest_on_pylon -> {
                        selectSpecies(fieldSpeciesNestOnPylon(), value as String)
                    }
                    R.string.monitoring_pylons_nest_type -> {
                        selectSingleChoice(fieldNestType(), value as String)
                    }
                    R.string.monitoring_pylons_pylon_insulated -> {
                        fieldPylonInsulated().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_pylons_damaged_insulation -> {
                        fieldDamagedInsulation().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_pylons_primary_habitat -> {
                        selectSingleChoice(fieldPrimaryHabitat(), value as String)
                    }
                    R.string.monitoring_pylons_secondary_habitat -> {
                        selectSingleChoice(fieldSecondaryHabitat(), value as String)
                    }
                    R.string.monitoring_pylons_notes -> {
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}