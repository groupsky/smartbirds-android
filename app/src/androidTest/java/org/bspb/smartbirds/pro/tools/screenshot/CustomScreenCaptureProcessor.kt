package org.bspb.smartbirds.pro.tools.screenshot

import android.app.Application
import android.os.Environment
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File

class CustomScreenCaptureProcessor() : BasicScreenCaptureProcessor() {
    init {
        mDefaultScreenshotPath =
            File(
                getApplicationContext<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "espresso_screenshots"
            )
    }
}
