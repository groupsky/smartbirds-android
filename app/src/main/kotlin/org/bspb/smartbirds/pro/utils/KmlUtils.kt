package org.bspb.smartbirds.pro.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import org.bspb.smartbirds.pro.ui.utils.Constants
import java.io.File

object KmlUtils {

    fun checkExistingUserKml(context: Context): Boolean {
        return getKmlFile(context).exists()
    }

    fun copyFileToExternalStorage(context: Context, uri: Uri) {
        context.contentResolver.openInputStream(uri)?.use {
            val kmlFile = getKmlFile(context)
            if (kmlFile.exists()) {
                kmlFile.delete()
            }
            it.copyTo(kmlFile.outputStream())
        }
    }

    private fun getKmlFile(context: Context): File {
        return File(context.getExternalFilesDir(null), Constants.AREA_FILE_NAME)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                context.contentResolver
                    .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            cursor.let {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(0)
                }
                it?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }
}