package org.bspb.smartbirds.pro.sync

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.googlecode.jcsv.CSVStrategy
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.androidannotations.annotations.res.StringRes
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.dto.UploadFormResponse
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.forms.convert.Converter
import org.bspb.smartbirds.pro.forms.upload.Uploader
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryParser
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.json.JSONObject
import retrofit2.Response
import java.io.*

@EBean(scope = EBean.Scope.Default)
open class UploadManager {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".UploadService"
        var isUploading = false
        var errors: ArrayList<String> = arrayListOf()
    }

    @RootContext
    protected lateinit var context: Context

    @Bean
    protected lateinit var backend: Backend

    private val monitoringManager = MonitoringManager.getInstance()

    @StringRes(R.string.tag_lat)
    protected lateinit var tagLatitude: String

    @StringRes(R.string.tag_lon)
    protected lateinit var tagLongitude: String

    suspend fun uploadAllNew() {
        Log.d(TAG, "uploading all finished monitorings")
        errors.clear()
        isUploading = true
        try {
            monitoringManager.monitoringCodesForStatus(Monitoring.Status.finished)?.forEach { monitoringCode ->
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

        if (monitoringDir.exists() && monitoringDir.isDirectory) {
            if (!uploadMonitoringFiles(monitoringDir.absolutePath, monitoringCode)) {
                hasErrors = true
            }

            if (!uploadMonitoringEntries(monitoringCode)) {
                hasErrors = true
            }
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

    private fun uploadMonitoringFiles(monitoringPath: String, monitoringCode: String): Boolean {
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
                Toast.makeText(
                    context,
                    String.format(
                        "Could not upload %s of %s to smartbirds.org!",
                        monitoringFile,
                        monitoringCode
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return success
    }

    private fun uploadMonitoringEntries(monitoringCode: String): Boolean {

    }

    suspend fun uploadAll() {
        Log.d(TAG, "uploading all finished monitorings")
        errors.clear()
        isUploading = true
        try {
            val baseDir = context.getExternalFilesDir(null)
            for (monitoringCode in monitoringManager.monitoringCodesForStatus(Monitoring.Status.finished)) {
                val monitoringDir = File(baseDir, monitoringCode)
                if (!monitoringDir.exists()) {
                    val msg = "Missing folder " + monitoringDir.path
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Reporting.logException(Exception(msg))
                    continue
                }
                if (!monitoringDir.isDirectory) {
                    val msg = monitoringDir.path + " is not a folder"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Reporting.logException(Exception(msg))
                    continue
                }
                upload(monitoringDir.absolutePath)
            }
        } finally {
            isUploading = false
        }
    }

    suspend fun upload(monitoringPath: String) {
        Log.d(TAG, String.format("uploading %s", monitoringPath))
        val file = File(monitoringPath)
        val monitoringName = file.name
        Log.d(TAG, String.format("uploading %s", monitoringName))

        try {
            uploadOnServer(monitoringPath, monitoringName)
            monitoringManager.updateStatus(monitoringName, Monitoring.Status.uploaded)
        } catch (e: Throwable) {
            Reporting.logException(e)
            Toast.makeText(
                context, String.format(
                    """
    Could not upload %s to server!
    You will need to manually export.
    """.trimIndent(), monitoringName
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(Exception::class)
    private fun uploadOnServer(monitoringPath: String, monitoringName: String) {
        val file = File(monitoringPath)

        // map between filenames and their ids
        val fileObjs: MutableMap<String, JsonObject> = HashMap()

        // first upload images
        var hasErrors = false

        for (subfile in file.list { dir, name -> name.matches("Pic\\d+\\.jpg".toRegex()) || "track.gpx" == name }) {
            try {
                fileObjs[subfile] = uploadFile(File(file, subfile))
            } catch (t: Throwable) {
                // Do not mark as error if the exception is related to image file. Sometimes we try to upload
                // invalid image files which are not related to any form record.
                if (subfile.endsWith("track.gpx")) {
                    hasErrors = true
                    errors.add(context.getString(R.string.sync_error_upload_file, subfile))
                }
                Reporting.logException(t)
                Toast.makeText(
                    context,
                    String.format(
                        "Could not upload %s of %s to smartbirds.org!",
                        subfile,
                        monitoringName
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // then upload forms
        for (subfile in file.list { dir, name -> name.matches(".*\\.csv".toRegex()) }) {
            try {
                uploadForm(monitoringName, file, subfile, fileObjs)
            } catch (t: Throwable) {
                hasErrors = true
            }
        }
        if (hasErrors) {
            throw IOException("Could not upload forms")
        }
    }

    @Throws(Exception::class)
    private fun uploadForm(
        monitoringName: String,
        base: File,
        filename: String,
        fileObjs: Map<String, JsonObject>
    ) {
        for (entryType in EntryType.values()) {
            if (entryType.filename.equals(filename, ignoreCase = true)) {
                uploadForm(
                    monitoringName,
                    File(base, filename),
                    entryType.getConverter(context),
                    entryType.uploader,
                    fileObjs
                )
                return
            }
        }
        Log.w(TAG, "unhandled form file: $filename")
    }

    @Throws(IOException::class)
    private fun uploadFile(file: File): JsonObject {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
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

    @Throws(Exception::class)
    private fun uploadForm(
        monitoringName: String,
        file: File,
        converter: Converter,
        uploader: Uploader,
        fileObjs: Map<String, JsonObject>
    ) {
        val fis = FileInputStream(file)
        fis.use { fis ->
            val csvReader =
                CSVReaderBuilder<Array<String>>(InputStreamReader(BufferedInputStream(fis))).strategy(
                    CSVStrategy.DEFAULT
                ).entryParser(SmartBirdsCSVEntryParser()).build()
            csvReader.use { csvReader ->
                val header = csvReader.readHeader()
                var hasErrors = false
                for (row in csvReader) {
                    val csv = HashMap<String, String>()
                    val it: Iterator<String> = header.iterator()
                    var columnName: String
                    run {
                        var idx = 0
                        while (it.hasNext() && idx < row.size) {
                            columnName = it.next()
                            csv[columnName] = row[idx]
                            idx++
                        }
                    }
                    val data = converter.convert(csv)
                    try {
                        check(!(data[tagLatitude].asDouble == 0.0 || data[tagLongitude].asDouble == 0.0))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Reporting.logException(IllegalStateException("Uploading entry with zero coordinates. $data"))
                    }

                    // convert pictures
                    val pictures = JsonArray()
                    var idx = 0
                    while (true) {
                        val fieldName = "Picture$idx"
                        idx++
                        if (!csv.containsKey(fieldName)) break
                        val filename = csv[fieldName]
                        if (TextUtils.isEmpty(filename)) continue
                        val fileObj = fileObjs[filename]
                        if (fileObj == null) {
                            hasErrors = true
                            val error = context.getString(
                                R.string.sync_error_missing_image,
                                filename,
                                monitoringName
                            )
                            errors.add(error)
                            Reporting.logException(IllegalStateException(error))
                            Toast.makeText(
                                context,
                                error,
                                Toast.LENGTH_SHORT
                            ).show()
                            continue
                        }
                        pictures.add(fileObj)
                    }
                    data.add("pictures", pictures)

                    // convert gpx
                    if (!fileObjs.containsKey("track.gpx")) {
                        val msg = "Missing track.gpx file for $monitoringName"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        Reporting.logException(IllegalStateException(msg))
                    } else {
                        data.add("track", fileObjs["track.gpx"]!!["url"])
                    }
                    val call = uploader.upload(backend.api(), data)

                    var response: Response<UploadFormResponse>? = null
                    var failed = false
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
                if (hasErrors) {
                    throw IOException("Couldn't upload form")
                }
            }
        }
    }
}