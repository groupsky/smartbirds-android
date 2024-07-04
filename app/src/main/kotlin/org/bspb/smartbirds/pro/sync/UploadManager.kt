package org.bspb.smartbirds.pro.sync

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

open class UploadManager(private val context: Context) {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".UploadService"
        var isUploading = false
        var errors: ArrayList<String> = arrayListOf()
    }

    protected val backend: Backend by lazy { Backend.getInstance() }

    private val monitoringManager = MonitoringManager.getInstance()

    suspend fun uploadAll() {
        Log.d(TAG, "uploading all finished monitorings")
        errors.clear()
        isUploading = true
        try {
            monitoringManager.monitoringCodesForStatus(Monitoring.Status.finished)
                .forEach { monitoringCode ->
                    uploadMonitoring(monitoringCode)
                }
        } finally {
            isUploading = false
        }
    }

    private suspend fun uploadMonitoring(monitoringCode: String) {
        val baseDir = context.getExternalFilesDir(null)
        val monitoringDir = File(baseDir, monitoringCode)
        var hasErrors = false

        var fileObjects: Map<String, JsonObject>? = null
        if (monitoringDir.exists() && monitoringDir.isDirectory) {
            try {
                fileObjects = uploadMonitoringFiles(monitoringDir.absolutePath, monitoringCode)
            } catch (t: Throwable) {
                hasErrors = true
            }
        } else {
            errors.add(context.getString(R.string.sync_error_missing_files, monitoringCode))
        }

        if (!uploadMonitoringEntries(monitoringCode, fileObjects)) {
            hasErrors = true
        }

        if (hasErrors) {
            Toast.makeText(
                context, String.format(
                    """
    Could not upload %s to server!
    You will need to manually export.
    """.trimIndent(), monitoringCode
                ),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        monitoringManager.updateStatus(monitoringCode, Monitoring.Status.uploaded)
    }

    @Throws(Exception::class)
    private fun uploadMonitoringFiles(
        monitoringPath: String,
        monitoringCode: String,
    ): Map<String, JsonObject> {
        val monitoringDir = File(monitoringPath)

        // map between filenames and their ids
        val fileObjs: MutableMap<String, JsonObject> = HashMap()

        // first upload images
        var success = true

        monitoringDir.list { _, name ->
            name.matches("Pic\\d+\\.jpg".toRegex()) || "track.gpx" == name
        }?.forEach { monitoringFile ->
            try {
                fileObjs[monitoringFile] = uploadFile(File(monitoringDir, monitoringFile))
            } catch (t: Throwable) {
                // Do not mark as error if the exception is related to image file. Sometimes we try to upload
                // invalid image files which are not related to any form record.
                if (monitoringFile.endsWith("track.gpx")) {
                    success = false
                    errors.add(context.getString(R.string.sync_error_upload_file, monitoringFile))
                }
                Reporting.logException(t)
//                Toast.makeText(
//                    context,
//                    String.format(
//                        "Could not upload %s of %s to smartbirds.org!",
//                        monitoringFile,
//                        monitoringCode
//                    ),
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }

        if (!success) {
            throw IOException("Could not upload form files")
        }

        return fileObjs
    }

    private suspend fun uploadMonitoringEntries(
        monitoringCode: String,
        fileObjects: Map<String, JsonObject>?,
    ): Boolean {
        val monitoring = monitoringManager.getMonitoring(monitoringCode)
        monitoring ?: return false
        var hasErrors = false

        val monitoringEntries = monitoringManager.getEntries(monitoring)

        monitoringEntries.forEach { dbEntry ->
            val monitoringEntry = MonitoringManager.entryFromDb(dbEntry)
            val dataValues = monitoringEntry.data.plus(monitoring.commonForm).mapValues {
                it.value.replace(
                    Pattern.quote(Configuration.MULTIPLE_CHOICE_DELIMITER).toRegex(),
                    "\n"
                )
            }
            val dataJson =
                monitoringEntry.type.getConverter(context).convert(dataValues)

            // convert pictures
            val pictures = JsonArray()
            var idx = 0
            while (true) {
                val fieldName = "Picture$idx"
                idx++
                if (!monitoringEntry.data.containsKey(fieldName)) break
                val filename = monitoringEntry.data[fieldName]
                if (TextUtils.isEmpty(filename)) continue
                val fileObj = fileObjects?.get(filename)
                if (fileObj == null) {
                    hasErrors = true
                    val error = context.getString(
                        R.string.sync_error_missing_image,
                        filename,
                        monitoringCode
                    )
                    errors.add(error)
                    Reporting.logException(IllegalStateException(error))
//                    Toast.makeText(
//                        context,
//                        error,
//                        Toast.LENGTH_SHORT
//                    ).show()
                    continue
                }
                pictures.add(fileObj)
            }
            dataJson.add("pictures", pictures)

            // convert gpx
            if (fileObjects == null || !fileObjects.containsKey("track.gpx")) {
                val msg = "Missing track.gpx file for $monitoringCode"
//                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                Reporting.logException(IllegalStateException(msg))
            } else {
                dataJson.add("track", fileObjects.get("track.gpx")!!["url"])
            }

            val call = monitoringEntry.type.uploader.upload(backend.api(), dataJson)

            var response: Response<UploadFormResponse>? = null
            var failed: Boolean
            try {
                response = call.execute()
                failed = !response.isSuccessful
            } catch (t: Throwable) {
                failed = true
            }

            if (failed) {
                hasErrors = true
                var error = ""
                if (response != null) {
                    try {
                        val errorBodyString = response.errorBody()!!.string()

                        error += response.code().toString() + ": " + response.message()
                        error += """
                                
                                $errorBodyString
                                """.trimIndent()

                        var errorJson = JSONObject(errorBodyString)

                        errors.add(errorJson.getString("error"))
                    } catch (t: Throwable) {
                        Reporting.logException(t)
                    }
                } else {
                    errors.add(context.getString(R.string.sync_error_upload_form))
                }
            }
        }

        return !hasErrors
    }

    @Throws(IOException::class)
    private fun uploadFile(file: File): JsonObject {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val call = backend.api().upload(body)
        val response = call.execute()
        if (!response.isSuccessful) {
            throw IOException("Server error: " + response.code() + " - " + response.message())
        }
        val fileObj = JsonObject()
        fileObj.addProperty(
            "url",
            String.format("%sstorage/%s", BuildConfig.BACKEND_BASE_URL, response.body()!!.data.id)
        )
        return fileObj
    }

}