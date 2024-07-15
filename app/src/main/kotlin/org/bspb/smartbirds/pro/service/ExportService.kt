package org.bspb.smartbirds.pro.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.googlecode.jcsv.CSVStrategy
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder
import kotlinx.coroutines.runBlocking
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.ExportFailedEvent
import org.bspb.smartbirds.pro.events.ExportPreparedEvent
import org.bspb.smartbirds.pro.tools.CsvPreparer
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryConverter
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.utils.MonitoringUtils
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExportService : IntentService("Export Service") {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".ExportService"
        private const val ACTION_PREPARE_FOR_EXPORT: String = "prepareForExport"
        private const val BUFFER = 2048

        fun newIntent(context: Context): Intent {
            return Intent(context, ExportService::class.java).apply {
                action = ACTION_PREPARE_FOR_EXPORT
            }
        }
    }

    private val eventBus: EEventBus by lazy { EEventBus.getInstance() }
    private val monitoringManager = MonitoringManager.getInstance()


    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        intent ?: return

        val action = intent.action
        if (ACTION_PREPARE_FOR_EXPORT == action) {
            prepareForExport()
            return
        }
    }

    private fun prepareForExport() {
        runBlocking {
            Log.d(TAG, "Prepare for export all finished monitorings")
            val exportFile = File(getExternalFilesDir(null), "export.zip")
            try {
                val exportOutStream = FileOutputStream(exportFile)
                val zipOut = ZipOutputStream(exportOutStream)
                val baseDir = getExternalFilesDir(null)
                val data = ByteArray(BUFFER)
                for (monitoringCode in monitoringManager.monitoringCodesForStatus(Monitoring.Status.finished)) {
                    val monitoring = monitoringManager.getMonitoring(monitoringCode)
                    monitoring ?: continue

                    generateEntriesCsvFiles(monitoring)
                    val monitoringDir = File(baseDir, monitoringCode)
                    if (!monitoringDir.exists()) continue
                    if (!monitoringDir.isDirectory) continue
                    monitoringDir.listFiles()?.let { files ->
                        for (fileToZip in files) {
                            val entry = ZipEntry(monitoringDir.name + "/" + fileToZip.name)
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
                }
                zipOut.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error while preparing zip for export", e)
                eventBus.post(ExportFailedEvent())
                return@runBlocking
            }
            val uri = FileProvider.getUriForFile(
                applicationContext,
                SmartBirdsApplication.FILES_AUTHORITY,
                exportFile
            )
            eventBus.post(ExportPreparedEvent(uri))
        }
    }

    private fun generateEntriesCsvFiles(monitoring: Monitoring) {
        try {
            val types = EntryType.values()
            for (entryType in types) {
                val entriesFile = getEntriesFile(monitoring, entryType)
                val commonLines = convertToCsvLines(monitoring.commonForm)
                val formEntries = monitoringManager.getEntries(monitoring, entryType)
                if (formEntries.isEmpty()) {
                    if (entriesFile.exists()) entriesFile.delete()
                    continue
                }
                val outWriter = BufferedWriter(FileWriter(entriesFile))
                outWriter.use { writer ->
                    var firstLine = true
                    val entries: MutableList<MonitoringEntry> = ArrayList()
                    for (formEntry in formEntries) {
                        entries.add(MonitoringManager.entryFromDb(formEntry))
                    }

                    // Retain only keys available in all entries
                    val normalizedKeys: MutableSet<String> = HashSet()
                    for (entry in entries) {
                        if (normalizedKeys.size == 0) {
                            // fill with initial values
                            normalizedKeys.addAll(entry.data.keys)
                        }
                        normalizedKeys.retainAll(entry.data.keys)
                    }
                    for (entry in entries) {

                        // Remove keys missing in other entries
                        entry.data.keys.retainAll(normalizedKeys)
                        val lines = convertToCsvLines(entry.data)
                        if (firstLine) {
                            writer.write(commonLines[0])
                            writer.write(CSVStrategy.DEFAULT.delimiter.code)
                            writer.write(lines[0])
                            writer.newLine()
                            firstLine = false
                        }
                        writer.write(commonLines[1])
                        writer.write(CSVStrategy.DEFAULT.delimiter.code)
                        writer.write(lines[1])
                        writer.newLine()
                    }
                }
            }
        } catch (t: Throwable) {
            Reporting.logException(t)
        }
    }

    @Throws(IOException::class)
    private fun convertToCsvLines(data: HashMap<String, String>): Array<String> {
        val prepared = CsvPreparer.prepareCsvLine(data)
        val memory = StringWriter()
        memory.use {
            val csvWriter = CSVWriterBuilder<Array<String>>(it).strategy(CSVStrategy.DEFAULT)
                .entryConverter(SmartBirdsCSVEntryConverter()).build()
            csvWriter.write(prepared.keys)
            csvWriter.write(prepared.values)
            it.flush()
        }
        val commonData = memory.buffer.toString()
        return commonData.split(System.lineSeparator()).toTypedArray()
    }

    private fun getEntriesFile(monitoring: Monitoring, entryType: EntryType): File {
        val filename = entryType.filename
        val file = File(MonitoringUtils.createMonitoringDir(this, monitoring), filename)
        file.setReadable(true)
        return file
    }
}