package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.action.SBViewActions.typeTextAndEnable
import org.bspb.smartbirds.pro.tools.fillTextField
import org.bspb.smartbirds.pro.tools.robot.BirdsFormTestRobot.Companion.birdsScreen
import org.bspb.smartbirds.pro.tools.selectMultipleChoice
import org.bspb.smartbirds.pro.tools.selectSingleChoice
import org.bspb.smartbirds.pro.tools.selectSpecies

class BirdsFormHelper {
    companion object {
        fun fillFormFields(fields: Map<Int, Any>) {
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            birdsScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        tabMain().perform(click())
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_private -> {
                        tabMain().perform(click())
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_name -> {
                        tabMain().perform(click())
                        selectSpecies(fieldSpecies(), value as String)
                    }
                    R.string.monitoring_birds_count_unit -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldCountUnit(), value as String)
                    }
                    R.string.monitoring_birds_count_type -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldCountType(), value as String)
                    }
                    R.string.monitoring_birds_count -> {
                        tabMain().perform(click())
                        fieldCount().perform(scrollTo(), typeTextAndEnable(value as String))
                    }
                    R.string.monitoring_birds_min -> {
                        tabMain().perform(click())
                        fieldCountMin().perform(scrollTo(), typeTextAndEnable(value as String))
                    }
                    R.string.monitoring_birds_max -> {
                        tabMain().perform(click())
                        fieldCountMax().perform(scrollTo(), typeTextAndEnable(value as String))
                    }
                    R.string.monitoring_birds_status -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldStatus(), value as String)
                    }
                    R.string.monitoring_birds_behaviour -> {
                        tabMain().perform(click())
                        selectMultipleChoice(fieldBehavior(), value as Array<String>)
                    }
                    R.string.monitoring_birds_gender -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldGender(), value as String)
                    }
                    R.string.monitoring_birds_age -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldAge(), value as String)
                    }
                    R.string.monitoring_birds_nesting -> {
                        tabMain().perform(click())
                        selectSingleChoice(fieldNestType(), value as String)
                    }
                    R.string.monitoring_birds_death -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldDeath(), value as String)
                    }
                    R.string.monitoring_birds_marking -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldMarking(), value as String)
                    }
                    R.string.monitoring_birds_substrate -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldSubstrate(), value as String)
                    }
                    R.string.monitoring_birds_tree -> {
                        tabOptional().perform(click())
                        fillTextField(fieldTree(), value as String)
                    }
                    R.string.monitoring_birds_tree_height -> {
                        tabOptional().perform(click())
                        fillTextField(fieldTreeHeight(), value as String)
                    }
                    R.string.monitoring_birds_tree_location -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldTreeLocation(), value as String)
                    }
                    R.string.monitoring_birds_nest_height -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldNestHeight(), value as String)
                    }
                    R.string.monitoring_birds_nest_location -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldNestLocation(), value as String)
                    }
                    R.string.monitoring_birds_incubation -> {
                        tabOptional().perform(click())
                        fieldIncubation().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_eggs_count -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNumberOfEggs(), value as String)
                    }
                    R.string.monitoring_birds_small_downy_count -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNumberOfPull(), value as String)
                    }
                    R.string.monitoring_birds_small_feathered_count -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNumberOfFledglings(), value as String)
                    }
                    R.string.monitoring_birds_takeoff_count -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNumberOfTakeoff(), value as String)
                    }
                    R.string.monitoring_birds_nest_guard -> {
                        tabOptional().perform(click())
                        fieldNestGuarding().perform(scrollTo(), setChecked(value as Boolean))
                    }
                    R.string.monitoring_birds_female_age -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldAgeOfFemale(), value as String)
                    }
                    R.string.monitoring_birds_male_age -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldAgeOfMale(), value as String)
                    }
                    R.string.monitoring_birds_nest_success -> {
                        tabOptional().perform(click())
                        selectSingleChoice(fieldNestSuccess(), value as String)
                    }
                    R.string.monitoring_birds_land_use -> {
                        tabOptional().perform(click())
                        fillTextField(fieldLandUse(), value as String)
                    }
                    R.string.monitoring_common_threats -> {
                        tabOptional().perform(click())
                        selectMultipleChoice(fieldThreats(), value as Array<String>)
                    }
                    R.string.monitoring_birds_notes -> {
                        tabOptional().perform(click())
                        fillTextField(fieldNotes(), value as String)
                    }
                }
            }

        }
    }
}