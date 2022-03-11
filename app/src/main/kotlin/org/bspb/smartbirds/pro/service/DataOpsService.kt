package org.bspb.smartbirds.pro.service

import android.content.Context
import android.util.Log
import com.googlecode.jcsv.CSVStrategy
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder
import kotlinx.coroutines.runBlocking
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.androidannotations.annotations.res.StringRes
import org.androidannotations.api.support.app.AbstractIntentService
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.tools.CsvPreparer
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SmartBirdsCSVEntryConverter
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.utils.MonitoringManager.Companion.entryFromDb
import java.io.*
import java.util.*

@EIntentService
open class DataOpsService : AbstractIntentService("DataOpsService") {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".DataOpsSvc"

        fun getMonitoringDir(context: Context, monitoringCode: String?): File {
            return File(context.getExternalFilesDir(null), monitoringCode)
        }

        fun createMonitoringDir(context: Context?, monitoring: Monitoring): File? {
            var dir = getMonitoringDir(context!!, monitoring.code)
            if (dir.exists()) return dir
            if (dir.mkdirs()) return dir
            Log.w(TAG, String.format("Cannot create %s", dir))
            dir = getMonitoringDir(context, monitoring.code)
            if (dir.exists()) return dir
            if (dir.mkdirs()) return dir
            Log.e(TAG, String.format("Cannot create %s", dir))
            return null
        }
    }

    private val monitoringManager = MonitoringManager.getInstance()

    @StringRes(R.string.tag_lat)
    protected lateinit var tagLatitude: String

    @StringRes(R.string.tag_lon)
    protected lateinit var tagLongitude: String

    @ServiceAction
    open fun generateMonitoringFiles(monitoringCode: String?) {
        try {
            Log.d(TAG, String.format(Locale.ENGLISH, "generateMonitoringFiles: %s", monitoringCode))
            runBlocking {
                monitoringManager.getMonitoring(monitoringCode!!)?.also {
                    combineCommonWithEntries(it)
                }
            }
        } catch (t: Throwable) {
            Reporting.logException(t)
        }
    }

    private fun combineCommonWithEntries(monitoring: Monitoring) {
        try {
            val types = EntryType.values()
            for (entryType in types) {
                val entriesFile = getEntriesFile(monitoring, entryType)
                val commonLines = convertToCsvLines(monitoring.commonForm)
                val formEntries = monitoringManager.getEntries(monitoring, entryType)
                if (formEntries == null || formEntries.isEmpty()) {
                    if (entriesFile.exists()) entriesFile.delete()
                    continue
                }
                val outWriter = BufferedWriter(FileWriter(entriesFile))
                try {
                    var firstLine = true
                    val entries: MutableList<MonitoringEntry> = ArrayList()
                    for (formEntry in formEntries) {
                        entries.add(entryFromDb(formEntry))
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
                        try {
                            check(
                                !(entry.data[tagLatitude]!!.toDouble() == 0.0 || entry.data[tagLongitude]!!
                                    .toDouble() == 0.0)
                            )
                        } catch (e: Exception) {
                            Reporting.logException(
                                IllegalStateException(
                                    "Saving in file entry " + entry.id + " with zero coordinates. Monitoring code is: " + entry.monitoringCode + " and type is " + entryType,
                                    e
                                )
                            )
                        }
                        val lines = convertToCsvLines(entry.data)
                        if (firstLine) {
                            outWriter.write(commonLines[0])
                            outWriter.write(CSVStrategy.DEFAULT.delimiter.toInt())
                            outWriter.write(lines[0])
                            outWriter.newLine()
                            firstLine = false
                        }
                        outWriter.write(commonLines[1])
                        outWriter.write(CSVStrategy.DEFAULT.delimiter.toInt())
                        outWriter.write(lines[1])
                        outWriter.newLine()
                    }
                } finally {
                    outWriter.close()
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
        memory.use { memory ->
            val csvWriter = CSVWriterBuilder<Array<String>>(memory).strategy(CSVStrategy.DEFAULT)
                .entryConverter(SmartBirdsCSVEntryConverter()).build()
            csvWriter.write(prepared.keys)
            csvWriter.write(prepared.values)
            memory.flush()
        }
        val commonData = memory.buffer.toString()
        return commonData.split(System.getProperty("line.separator")).toTypedArray()
    }

    private fun getEntriesFile(monitoring: Monitoring, entryType: EntryType): File {
        val filename = entryType.filename
        val file = File(createMonitoringDir(this, monitoring), filename)
        file.setReadable(true)
        return file
    }

}