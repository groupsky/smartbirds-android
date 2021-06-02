package org.bspb.smartbirds.pro.tools.screenshot

import android.app.Application
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File

class CustomScreenCaptureProcessor() : BasicScreenCaptureProcessor() {
    init {
        mDefaultScreenshotPath =
            File(
                File(
                    mDefaultScreenshotPath,
                    getApplicationContext<Application>().packageName,
                ),
                "espresso_screenshots"
            )
    }
}
