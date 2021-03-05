package org.bspb.smartbirds.pro.service

import android.app.IntentService
import android.content.Intent
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
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.androidannotations.annotations.res.StringRes
import org.bspb.smartbirds.pro.BuildConfig
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.Monitoring.Status.finished
import org.bspb.smartbirds.pro.content.Monitoring.Status.uploaded
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.StartingUpload
import org.bspb.smartbirds.pro.events.UploadCompleted
import org.bspb.smartbirds.pro.forms.convert.Converter
import org.bspb.smartbirds.pro.forms.upload.Uploader
import org.bspb.smartbirds.pro.tools.Reporting.logException
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryParser
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean
import org.bspb.smartbirds.pro.utils.debugLog
import java.io.*
import java.util.*


@EIntentService
open class UploadService : IntentService {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".UploadService"
        var isUploading = false
    }

    @Bean
    protected lateinit var eventBus: EEventBus

    @Bean
    protected lateinit var backend: Backend

    @Bean
    protected lateinit var nomenclaturesBean: NomenclaturesBean

    @Bean
    protected lateinit var monitoringManager: MonitoringManager

    @StringRes(R.string.tag_lat)
    protected lateinit var tagLatitude: String

    @StringRes(R.string.tag_lon)
    protected lateinit var tagLongitude: String

    protected constructor() : super("Upload service") {
    }

    override fun onHandleIntent(intent: Intent?) {
    }

    @ServiceAction
    open fun uploadAll() {
        Log.d(TAG, "uploading all finished monitorings")
        isUploading = true
        eventBus.post(StartingUpload())
        try {
            val baseDir = getExternalFilesDir(null)
            for (monitoringCode in monitoringManager.monitoringCodesForStatus(finished)) {
                val monitoringDir = File(baseDir, monitoringCode)
                if (!monitoringDir.exists()) {
                    val msg = "Missing folder " + monitoringDir.path
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    logException(Exception(msg))
                    continue
                }
                if (!monitoringDir.isDirectory) {
                    val msg = monitoringDir.path + " is not a folder"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    logException(Exception(msg))
                    continue
                }
                upload(monitoringDir.absolutePath)
            }
        } finally {
            isUploading = false
            eventBus.post(UploadCompleted())
        }
    }

    @ServiceAction
    open fun upload(monitoringPath: String) {
        Log.d(TAG, String.format("uploading %s", monitoringPath))
        val file = File(monitoringPath)
        val monitoringName = file.name
        Log.d(TAG, String.format("uploading %s", monitoringName))

        try {
            uploadOnServer(monitoringPath, monitoringName)
            monitoringManager.updateStatus(monitoringName, uploaded)
        } catch (e: Throwable) {
            logException(e)
            Toast.makeText(
                    this, String.format("""
    Could not upload %s to server!
    You will need to manually export.
    """.trimIndent(), monitoringName),
                    Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(Exception::class)
    private fun uploadOnServer(monitoringPath: String, monitoringName: String) {
        val file = File(monitoringPath)

        // map between filenames and their ids
        val fileObjs: MutableMap<String, JsonObject> = HashMap()

        // first upload images
        for (subfile in file.list { dir, name -> name.matches("Pic\\d+\\.jpg".toRegex()) || "track.gpx" == name }) {
            try {
                fileObjs[subfile] = uploadFile(File(file, subfile))
            } catch (t: Throwable) {
                logException(t)
                Toast.makeText(this, String.format("Could not upload %s of %s to smartbirds.org!", subfile, monitoringName),
                        Toast.LENGTH_SHORT).show()
            }
        }
//        if (!fileObjs.containsKey("track.gpx") && File(file, "track.gpx").exists()) {
//            // try again
//            fileObjs["track.gpx"] = uploadFile(File(file, "track.gpx"))
//        }


        // then upload forms
        for (subfile in file.list { dir, name -> name.matches(".*\\.csv".toRegex()) }) {
            uploadForm(monitoringName, file, subfile, fileObjs)
        }
    }

    @Throws(Exception::class)
    private fun uploadForm(monitoringName: String, base: File, filename: String, fileObjs: Map<String, JsonObject>) {
        for (entryType in EntryType.values()) {
            if (entryType.filename.equals(filename, ignoreCase = true)) {
                uploadForm(monitoringName, File(base, filename), entryType.getConverter(this), entryType.uploader, fileObjs)
                return
            }
        }
        Log.w(TAG, "unhandled form file: $filename")
    }

    @Throws(IOException::class)
    private fun uploadFile(file: File): JsonObject {
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val call = backend.api().upload(body)
        val response = call.execute()
        if (!response.isSuccessful) {
            throw IOException("Server error: " + response.code() + " - " + response.message())
        }
        val fileObj = JsonObject()
        fileObj.addProperty("url", String.format("%sstorage/%s", BuildConfig.BACKEND_BASE_URL, response.body()!!.data.id))
        return fileObj
    }

    @Throws(Exception::class)
    private fun uploadForm(monitoringName: String, file: File, converter: Converter, uploader: Uploader, fileObjs: Map<String, JsonObject>) {
        val fis = FileInputStream(file)
        try {
            val csvReader = CSVReaderBuilder<Array<String>>(InputStreamReader(BufferedInputStream(fis))).strategy(CSVStrategy.DEFAULT).entryParser(SmartBirdsCSVEntryParser()).build()
            try {
                val header = csvReader.readHeader()
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
                        logException(IllegalStateException("Uploading entry with zero coordinates. $data"))
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
                            val error = String.format("Missing image %s for %s", filename, monitoringName)
                            logException(IllegalStateException(error))
                            Toast.makeText(this,
                                    error,
                                    Toast.LENGTH_SHORT).show()
                            continue
                        }
                        pictures.add(fileObj)
                    }
                    data.add("pictures", pictures)

                    // convert gpx
                    if (!fileObjs.containsKey("track.gpx")) {
                        val msg = "Missing track.gpx file for $monitoringName"
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        logException(IllegalStateException(msg))
                    } else {
                        data.add("track", fileObjs["track.gpx"]!!["url"])
                    }
                    val call = uploader.upload(backend.api(), data)
                    val response = call.execute()
                    if (!response.isSuccessful) {
                        var error = ""
                        try {
                            error += response.code().toString() + ": " + response.message()
                            error += """
                            
                            ${response.errorBody()!!.string()}
                            """.trimIndent()
                        } catch (t: Throwable) {
                            logException(t)
                        }
                        throw IOException("Couldn't upload form: $error")
                    }
                }
            } finally {
                csvReader.close()
            }
        } finally {
            fis.close()
        }
    }
}