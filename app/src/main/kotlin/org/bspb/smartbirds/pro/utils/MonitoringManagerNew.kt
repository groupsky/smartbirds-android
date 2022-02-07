package org.bspb.smartbirds.pro.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.location.Location
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.content.MonitoringManager
import org.bspb.smartbirds.pro.content.TrackingLocation
import org.bspb.smartbirds.pro.db.SmartBirdsProvider
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.repository.FormRepository
import org.bspb.smartbirds.pro.repository.MonitoringRepository
import org.bspb.smartbirds.pro.room.Form
import org.bspb.smartbirds.pro.room.MonitoringModel
import org.bspb.smartbirds.pro.room.Tracking
import org.bspb.smartbirds.pro.tools.SBGsonParser
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MonitoringManagerNew private constructor(val context: Context) {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".MonitoringManager"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: MonitoringManagerNew? = null

        private val DATE_FORMATTER: DateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        private val SERIALIZER = SBGsonParser.createParser()

        fun getInstance(): MonitoringManagerNew {
            checkNotNull(INSTANCE) { "MonitoringManager instance is null. init(Context context) must be called before getting the instance." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                INSTANCE = MonitoringManagerNew(context)
                INSTANCE
            }
        }

        fun entryFromDb(form: Form): MonitoringEntry {
            val entry = SERIALIZER.fromJson(
                String(form.data),
                MonitoringEntry::class.java
            )
            entry.id = form.id.toLong()
            return entry
        }
    }

    private var tagLatitude: String? = null
    private var tagLongitude: String? = null
    private val formsRepository: FormRepository
    private val monitoringRepository: MonitoringRepository

    init {
        DATE_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        tagLatitude = context.getString(R.string.tag_lat)
        tagLongitude = context.getString(R.string.tag_lon)
        formsRepository = FormRepository()
        monitoringRepository = MonitoringRepository()
    }

    suspend fun createNew(): Monitoring {
        val code = generateMonitoringCode()
        val monitoring = MonitoringModel(code = code)
        monitoring.id = monitoringRepository.deleteMonitoring()
        return monitoring
    }

    fun newEntry(monitoring: Monitoring, entryType: EntryType, data: HashMap<String?, String?>) {
        val entry = MonitoringEntry(monitoring.code, entryType)
        entry.data.putAll(data)
        GlobalScope.launch(Dispatchers.IO) {
            formsRepository.insertForm(toDbModel(entry))
        }
    }

    fun updateEntry(
        monitoringCode: String,
        entryId: Long,
        entryType: EntryType,
        data: HashMap<String?, String?>
    ) {
        val entry = MonitoringEntry(monitoringCode, entryType)
        entry.data.putAll(data)
        entry.id = entryId

        GlobalScope.launch(Dispatchers.IO) {
            formsRepository.updateForm(toDbModel(entry))
        }
    }

    fun newTracking(monitoring: Monitoring, location: Location?): TrackingLocation? {
        /* TODO Enable tracking db persistence if needed
          Pause tracking db persistence since it is never read
         */
//        val repo = TrackingRepository()
//        repo.insertNewTracking(toDbModel(l))
        return TrackingLocation(monitoring.code, location!!)
    }

    fun deleteLastEntry(monitoring: Monitoring) {
        GlobalScope.launch(Dispatchers.IO) {
            val deletedRows = formsRepository.deleteLastEntry(monitoring.code)
            if (deletedRows.equals(0)) {
                Log.e(TAG, "could not delete last monitoring entry")
            }
        }
    }

    fun deleteEntries(ids: LongArray) {
        GlobalScope.launch(Dispatchers.IO) {
            formsRepository.deleteLastEntries(ids)
        }
    }

    fun deleteMonitoring(monitoringCode: String) {
        GlobalScope.launch(Dispatchers.IO) {
            monitoringRepository.deleteMonitoring(monitoringCode)
        }
    }

    fun getEntries(monitoring: Monitoring, entryType: EntryType): List<Form> {
        return formsRepository.getEntries(monitoring.code, entryType.name)
    }

    private fun toDbModel(entry: MonitoringEntry): Form {
        return Form(
            0,
            entry.monitoringCode,
            entry.type.name,
            entry.data[tagLatitude]!!.toDouble(),
            entry.data[tagLongitude]!!.toDouble(),
            SERIALIZER.toJson(entry).toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun toDbModel(location: TrackingLocation): Tracking {
        return Tracking(
            0,
            location.monitoringCode,
            location.time,
            location.latitude,
            location.longitude,
            location.altitude
        )
    }

    private fun generateMonitoringCode(): String {
        val uuid = UUID.randomUUID().toString()
        return String.format(
            "%s-%s", DATE_FORMATTER.format(Date()), uuid.substring(uuid.length - 12)
        )
    }
}