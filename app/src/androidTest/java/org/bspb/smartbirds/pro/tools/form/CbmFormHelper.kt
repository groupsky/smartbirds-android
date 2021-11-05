package org.bspb.smartbirds.pro.tools.form

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.*
import org.bspb.smartbirds.pro.tools.action.SBViewActions.setChecked
import org.bspb.smartbirds.pro.tools.action.SBViewActions.typeTextAndEnable
import org.bspb.smartbirds.pro.tools.form.entry.FormEntry
import org.bspb.smartbirds.pro.tools.robot.CbmFormTestRobot.Companion.cbmScreen

class CbmFormHelper {
    companion object {
        fun fillFormFields(entry: FormEntry) {
            var fields: Map<Int, Any> = entry.toUiMap()
            fields.forEach { (key, value) ->
                fillFormField(key, value)
            }
        }

        private fun fillFormField(key: Int, value: Any) {
            cbmScreen {
                when (key) {
                    R.string.monitoring_moderator_review -> {
                        fieldModeratorReview().perform(scrollTo(), setChecked(value as Boolean))
                    }

                    R.string.monitoring_birds_private -> {
                        fieldConfidential().perform(scrollTo(), setChecked(value as Boolean))
                    }

                    R.string.monitoring_cbm_name -> {
                        selectSpecies(fieldSpecies(), value as String)
                    }

                    R.string.monitoring_cbm_count -> {
                        fieldCount().perform(scrollTo(), typeTextAndEnable(value as String))
                    }

                    R.string.monitoring_cbm_distance -> {
                        onView(ViewMatchers.withText(value as String)).perform(
                            scrollTo(), click()
                        )
                    }

                    R.string.monitoring_cbm_zone -> {
                        selectZone(fieldZone(), value as String)
                    }

                    R.string.monitoring_common_threats -> {
                        selectMultipleChoice(fieldThreats(), (value as List<String>).toTypedArray())
                    }
                }
            }

        }
    }
}