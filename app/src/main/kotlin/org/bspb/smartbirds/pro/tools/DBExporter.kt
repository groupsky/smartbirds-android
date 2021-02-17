package org.bspb.smartbirds.pro.tools

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.utils.showAlert
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
                            val baseOutputDir = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + File.separator.toString() + "smartbirdspro")
                            if (!baseOutputDir.exists()) {
                                baseOutputDir.mkdir()
                            }
                            val dbOutputDir = File(baseOutputDir.absolutePath + File.separator.toString() + "db")
                            if (!dbOutputDir.exists()) {
                                dbOutputDir.mkdir()
                            }

                            val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                            val currentDbDir = File(dbOutputDir, dateFormat.format(Date()) + "")
                            if (!currentDbDir.exists()) {
                                currentDbDir.mkdir()
                            }
                            for (file in dbFiles) {
                                if (file.name.startsWith("smartBirdsDatabase")) {
                                    val out = File(currentDbDir, file.name)
                                    out.createNewFile()
                                    copy(file, out)
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
                context.showAlert(R.string.export_db_dialog_title_success, R.string.export_db_dialog_message_success, null, null)
            } else {
                context.showAlert(R.string.export_db_dialog_title_fail, R.string.export_db_dialog_message_fail, null, null)
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
    }
}