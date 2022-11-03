package org.bspb.smartbirds.pro.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtils {
    fun copyUriContentToFile(context: Context, uri: Uri?, file: File?) {
        uri ?: return
        file ?: return

        context.contentResolver.openInputStream(uri)?.use {
            if (file.exists()) {
                file.delete()
            }
            it.copyTo(file.outputStream())
        }
    }
}