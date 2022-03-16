package org.bspb.smartbirds.pro.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.service.ExportService
import org.bspb.smartbirds.pro.tools.GpxWriter
import org.bspb.smartbirds.pro.tools.Reporting
import java.io.*

class MonitoringUtils {
    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".MonitoringUtils"

        @JvmStatic
        fun getMonitoringDir(context: Context, monitoringCode: String?): File {
            return File(context.getExternalFilesDir(null), monitoringCode)
        }

        @JvmStatic
        fun createMonitoringDir(context: Context, monitoring: Monitoring): File? {
            var dir = getMonitoringDir(context, monitoring.code)
            if (dir.exists()) return dir
            if (dir.mkdirs()) return dir
            Log.w(TAG, String.format("Cannot create %s", dir))
            dir = getMonitoringDir(context, monitoring.code)
            if (dir.exists()) return dir
            if (dir.mkdirs()) return dir
            Log.e(TAG, String.format("Cannot create %s", dir))
            return null
        }

        @JvmStatic
        fun closeGpxFile(context: Context, monitoring: Monitoring) {
            val file = File(createMonitoringDir(context, monitoring), "track.gpx")
            try {
                val osw: Writer = BufferedWriter(FileWriter(file, true))
                osw.use { osw ->
                    GpxWriter(osw).writeFooter()
                }
            } catch (e: IOException) {
                Reporting.logException(e)
                Toast.makeText(context, "Could not write to track.gpx!", Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun initGpxFile(context: Context, monitoring: Monitoring): Boolean {
            val file = File(createMonitoringDir(context, monitoring), "track.gpx")
            try {
                val osw = OutputStreamWriter(BufferedOutputStream(FileOutputStream(file, false)))
                osw.use { osw ->
                    val writer = GpxWriter(osw)
                    writer.writeHeader()
                }
            } catch (e: IOException) {
                Reporting.logException(e)
                return false
            }
            return true
        }
    }
}