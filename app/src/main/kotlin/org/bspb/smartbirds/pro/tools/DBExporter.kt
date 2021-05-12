package org.bspb.smartbirds.pro.tools

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.utils.showAlert
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*


class DBExporter {

    companion object {
        fun exportDB(context: Context) {
            ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
            var success = false
            try {
                val dbFile: File = context.getDatabasePath("smartBirdsDatabase.db")
                val dbDir: File? = dbFile.parentFile
                if (dbDir != null) {
                    if (dbDir.isDirectory && dbDir.exists()) {
                        val dbFiles: Array<File>? = dbDir.listFiles()
                        if (dbFiles != null) {
                            for (file in dbFiles) {
                                if (file.name.startsWith("smartBirdsDatabase")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        exportFile(context, file)
                                    } else {
                                        exportLegacyFile(file)
                                    }
                                }
                            }
                            success = true
                        }
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            if (success) {
                context.showAlert(
                    R.string.export_db_dialog_title_success,
                    R.string.export_db_dialog_message_success,
                    null,
                    null
                )
            } else {
                context.showAlert(
                    R.string.export_db_dialog_title_fail,
                    R.string.export_db_dialog_message_fail,
                    null,
                    null
                )
            }
        }

        @Throws(IOException::class)
        private fun copy(src: File, dst: File) {
            FileInputStream(src).use { inputStream ->
                FileOutputStream(dst).use { out ->
                    // Transfer bytes from in to out
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
        }

        private fun copyFileData(context: Context, destinationContentUri: Uri, fileToExport: File) {
            context.contentResolver.openFileDescriptor(destinationContentUri, "w")
                .use { parcelFileDescriptor ->
                    ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor)
                        .write(fileToExport.readBytes())
                }
        }

        private fun createLegacyOutputDir(): File {
            val baseOutputDir = File(
                Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_DOWNLOADS + File.separator.toString() + "smartbirdspro"
            )
            if (!baseOutputDir.exists()) {
                baseOutputDir.mkdir()
            }
            val dbOutputDir =
                File(baseOutputDir.absolutePath + File.separator.toString() + "db")
            if (!dbOutputDir.exists()) {
                dbOutputDir.mkdir()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
            val currentDbDir = File(dbOutputDir, dateFormat.format(Date()) + "")
            if (!currentDbDir.exists()) {
                currentDbDir.mkdir()
            }
            return currentDbDir
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun exportFile(context: Context, fileToExport: File) {
            val downloadsLocation =
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
            val relativeLocation =
                "${Environment.DIRECTORY_DOWNLOADS}/smartbirdspro/db/${
                    dateFormat.format(Date())
                }"

            val contentDetails = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileToExport.name)
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    relativeLocation
                )
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val contentUri = context.contentResolver.insert(
                downloadsLocation,
                contentDetails
            )
            contentUri?.let { insertedContentUri ->
                copyFileData(context, insertedContentUri, fileToExport)
                contentDetails.clear()
                contentDetails.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(
                    insertedContentUri,
                    contentDetails,
                    null,
                    null
                )
            }
        }

        private fun exportLegacyFile(fileToExport: File) {
            val currentDbDir = createLegacyOutputDir()
            val out = File(currentDbDir, fileToExport.name)
            out.createNewFile()
            copy(fileToExport, out)
        }
    }
}