package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.fillDate
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.CiconiaFormTestRobot.Companion.ciconiaScreen
import org.bspb.smartbirds.pro.tools.selectMultipleChoice
import org.bspb.smartbirds.pro.tools.selectSingleChoice

class CiconiaFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            ciconiaScreen {
                when (key) {
                    R.string.monitoring_ciconia_substratum -> {
                        selectSingleChoice(fieldSubstrate(), value as String)
                    }
                    R.string.monitoring_ciconia_column -> {
                        selectSingleChoice(fieldPylon(), value as String)
                    }
                    R.string.monitoring_ciconia_nest_artificial -> {
                        fieldNestArtificial().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_ciconia_column_type -> {
                        selectSingleChoice(fieldPylonType(), value as String)
                    }
                    R.string.monitoring_ciconia_tree -> {
                        selectSingleChoice(fieldTree(), value as String)
                    }
                    R.string.monitoring_ciconia_building -> {
                        selectSingleChoice(fieldBuilding(), value as String)
                    }
                    R.string.monitoring_ciconia_nest_artificial_human -> {
                        fieldNestArtificialHuman().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_ciconia_nest_other -> {
                        fillTextField(fieldNestAnotherSubstrate(), value as String)
                    }
                    R.string.monitoring_ciconia_not_occupied -> {
                        selectSingleChoice(
                            fieldNestThisYearNotUtilizedByWhiteStorks(),
                            value as String
                        )
                    }
                    R.string.monitoring_ciconia_new_birds -> {
                        selectSingleChoice(
                            fieldThisYearOneTwoBirdsAppearedInNest(),
                            value as String
                        )
                    }
                    R.string.monitoring_ciconia_date_appear -> {
                        fillDate(fieldApproximateDateStorksAppeared(), value as String)
                    }
                    R.string.monitoring_ciconia_disappear -> {
                        fillDate(fieldApproximateDateDisappearanceWhiteStorks(), value as String)
                    }
                    R.string.monitoring_ciconia_this_year -> {
                        selectSingleChoice(fieldThisYearInTheNestAppeared(), value as String)
                    }
                    R.string.monitoring_ciconia_small_count -> {
                        fillTextField(fieldCountJuvenilesInNest(), value as String)
                    }
                    R.string.monitoring_ciconia_not_occupied_more_than_year -> {
                        fillTextField(fieldNestNotUsedForOverOneYear(), value as String)
                    }
                    R.string.monitoring_ciconia_small_death_electricity -> {
                        fillTextField(
                            fieldDataOnJuvenileMortalityFromElectrocutions(),
                            value as String
                        )
                    }
                    R.string.monitoring_ciconia_small_death_from_parent -> {
                        fillTextField(fieldDataOnJuvenilesExpelledFromParents(), value as String)
                    }
                    R.string.monitoring_ciconia_small_death_other -> {
                        fillTextField(fieldDiedOtherReasons(), value as String)
                    }
                    R.string.monitoring_ciconia_cause -> {
                        fillTextField(fieldCause(), value as String)
                    }
                    R.string.monitoring_common_threats -> {
                        selectMultipleChoice(fieldThreats(), (value as List<String>).toTypedArray())
                    }
                    R.string.monitoring_ciconia_notes -> {
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}