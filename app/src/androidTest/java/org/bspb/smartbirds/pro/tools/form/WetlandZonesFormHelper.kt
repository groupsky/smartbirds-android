package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.action.ViewActions.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.action.SBViewActions.typeTextAndEnable
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot.Companion.singleChoiceDialog
import org.bspb.smartbirds.pro.tools.robot.WetlandZonesFormTestRobot.Companion.wetlandScreen

class WetlandZonesFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        // Currently support only one record in the form
        private fun fillFormField(key: Int, value: Any) {
            wetlandScreen {
                when (key) {
                    R.string.monitoring_birds_name -> {
                        fieldSpecies(0).perform(click())
                        singleChoiceDialog {
                            onSpeciesRow(value as String).perform(scrollTo(), click())
                        }
                    }
                    R.string.monitoring_birds_count -> {
                        fieldCount(0).perform(clearText())
                        fieldCount(0).perform(typeTextAndEnable(value as String))
                        fieldCount(0).perform(closeSoftKeyboard())
                    }
                    R.string.monitoring_birds_gender -> {
                        fieldGender(0).perform(click())
                        singleChoiceDialog {
                            onRow(value as String).perform(scrollTo(), click())
                        }
                    }
                    R.string.monitoring_birds_age -> {
                        fieldAge(0).perform(click())
                        singleChoiceDialog {
                            onRow(value as String).perform(scrollTo(), click())
                        }
                    }
                }
            }

        }
    }
}