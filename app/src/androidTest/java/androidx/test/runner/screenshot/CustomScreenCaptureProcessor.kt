package androidx.test.runner.screenshot

import android.app.Application
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import java.io.File

class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor(
        File(
                getApplicationContext<Application>().getExternalFilesDir(DIRECTORY_PICTURES),
                "espresso_screenshots"
        )
)
