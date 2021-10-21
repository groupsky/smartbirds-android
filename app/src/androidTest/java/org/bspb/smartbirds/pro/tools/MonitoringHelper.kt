package org.bspb.smartbirds.pro.tools

import androidx.test.espresso.action.ViewActions.click
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.tools.robot.MonitoringTestRobot.Companion.monitoringScreen
import org.bspb.smartbirds.pro.tools.robot.SingleChoiceDialogTestRobot

class MonitoringHelper {
    companion object {
        fun finishMonitoring() {
            monitoringScreen {
                buttonFinish().perform(click())
            }
            commonFormScreen {
                fieldObservationMethodology().perform(click())
                SingleChoiceDialogTestRobot.singleChoiceDialog {
                    listItem(0).perform(click())
                }
                buttonFinish().perform(click())
            }
        }
    }
}