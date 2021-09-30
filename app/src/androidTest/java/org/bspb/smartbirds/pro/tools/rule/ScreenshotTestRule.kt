package org.bspb.smartbirds.pro.tools.rule

import android.graphics.Bitmap
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import org.bspb.smartbirds.pro.tools.screenshot.CustomScreenCaptureProcessor
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException
import java.util.*


class ScreenshotTestRule : TestWatcher() {
    override fun failed(e: Throwable?, description: Description) {
        super.failed(e, description)
        takeScreenshot(description)
    }

    private fun takeScreenshot(description: Description) {
        val filename: String = description.testClass.simpleName + "-" + description.methodName
        val capture: ScreenCapture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG
        val processors: HashSet<ScreenCaptureProcessor> = HashSet<ScreenCaptureProcessor>()
        processors.add(CustomScreenCaptureProcessor())
        try {
            capture.process(processors)
            println("Screenshot saved in "+capture.name)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}