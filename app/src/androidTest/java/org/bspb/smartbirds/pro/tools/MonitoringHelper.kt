package org.bspb.smartbirds.pro.tools

import androidx.test.espresso.action.ViewActions.click
import okhttp3.mockwebserver.MockWebServer
import org.bspb.smartbirds.pro.tools.robot.CommonFormTestRobot.Companion.commonFormScreen
import org.bspb.smartbirds.pro.tools.robot.MainTestRobot
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

        fun uploadData(server: MockWebServer) {
            // Upload record
            // mock sync responses. We need to enqueue all the response, so the sync dialog can disappear.
            // Otherwise the test freezes.
            val nomenclaturesResponse = MockResponseHelper.prepareNomenclatureResponse()
            server.enqueue(MockResponseHelper.prepareUploadFileResponse())
            server.enqueue(MockResponseHelper.prepareUploadFormRespons())
            server.enqueue(MockResponseHelper.prepareSuccessLoginResponse())
            server.enqueue(nomenclaturesResponse)
            server.enqueue(nomenclaturesResponse)

            MainTestRobot.mainScreen {
                buttonSync().perform(click())
            }

            // Consume first request with track.gpx data
            server.takeRequest()
        }
    }
}