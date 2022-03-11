package org.bspb.smartbirds.pro.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.runBlocking
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.ExportFailedEvent
import org.bspb.smartbirds.pro.events.ExportPreparedEvent
import org.bspb.smartbirds.pro.utils.MonitoringManager
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@EIntentService
open class ExportService : IntentService("Export Service") {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".ExportService"

        private const val BUFFER = 2048
    }

    @Bean
    protected lateinit var eventBus: EEventBus

    val monitoringManager = MonitoringManager.getInstance()

    override fun onHandleIntent(intent: Intent?) {
    }

    @ServiceAction
    open fun prepareForExport() {
        runBlocking {
            Log.d(TAG, "Prepare for export all finished monitorings")
            val exportFile = File(getExternalFilesDir(null), "export.zip")
            try {
                val exportOutStream = FileOutputStream(exportFile)
                val zipOut = ZipOutputStream(exportOutStream)
                val baseDir = getExternalFilesDir(null)
                val data = ByteArray(BUFFER)
                for (monitoringCode in monitoringManager.monitoringCodesForStatus(Monitoring.Status.finished)) {
                    val monitoring = File(baseDir, monitoringCode)
                    if (!monitoring.exists()) continue
                    if (!monitoring.isDirectory) continue
                    for (fileToZip in monitoring.listFiles()) {
                        val entry = ZipEntry(monitoring.name + "/" + fileToZip.name)
                        zipOut.putNextEntry(entry)
                        val inputStream = FileInputStream(fileToZip)
                        val `in` = BufferedInputStream(inputStream)
                        var count: Int
                        while (`in`.read(data, 0, BUFFER).also { count = it } != -1) {
                            zipOut.write(data, 0, count)
                        }
                        zipOut.closeEntry()
                        `in`.close()
                    }
                }
                zipOut.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error while preparing zip for export", e)
                eventBus.post(ExportFailedEvent())
                return@runBlocking
            }
            val uri = FileProvider.getUriForFile(applicationContext, SmartBirdsApplication.FILES_AUTHORITY, exportFile)
            eventBus.post(ExportPreparedEvent(uri))
        }
    }
}