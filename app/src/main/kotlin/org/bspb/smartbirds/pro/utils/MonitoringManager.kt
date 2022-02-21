package org.bspb.smartbirds.pro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.content.TrackingLocation
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

class MonitoringManager private constructor(val context: Context) {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".MonitoringManager"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: MonitoringManager? = null

        private val DATE_FORMATTER: DateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        private val SERIALIZER = SBGsonParser.createParser()

        fun getInstance(): MonitoringManager {
            checkNotNull(INSTANCE) { "MonitoringManager instance is null. init(Context context) must be called before getting the instance." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                INSTANCE = MonitoringManager(context)
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

        fun monitoringFromDb(dbModel: MonitoringModel?): Monitoring? {
            dbModel ?: return null

            val monitoring = SERIALIZER.fromJson(
                String(dbModel.data!!),
                Monitoring::class.java
            )
            monitoring.id = dbModel.id
            monitoring.status = monitoring.status

            return monitoring
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

    suspend fun newEntry(monitoring: Monitoring, entryType: EntryType, data: HashMap<String?, String?>) {
        val entry = MonitoringEntry(monitoring.code, entryType)
        entry.data.putAll(data)
        formsRepository.insertForm(toDbModel(entry))
    }

    suspend fun updateEntry(
        monitoringCode: String,
        entryId: Long,
        entryType: EntryType,
        data: HashMap<String?, String?>
    ) {
        val entry = MonitoringEntry(monitoringCode, entryType)
        entry.data.putAll(data)
        entry.id = entryId

        formsRepository.updateForm(toDbModel(entry))
    }

    fun newTracking(monitoring: Monitoring, location: Location?): TrackingLocation? {
        /* TODO Enable tracking db persistence if needed
          Pause tracking db persistence since it is never read
         */
//        val repo = TrackingRepository()
//        repo.insertNewTracking(toDbModel(l))
        return TrackingLocation(monitoring.code, location!!)
    }

    suspend fun deleteLastEntry(monitoring: Monitoring) {
        val deletedRows = formsRepository.deleteLastEntry(monitoring.code)
        if (deletedRows.equals(0)) {
            Log.e(TAG, "could not delete last monitoring entry")
        }
    }

    suspend fun deleteEntries(ids: LongArray) {
        formsRepository.deleteLastEntries(ids)
    }

    fun getEntries(monitoring: Monitoring, entryType: EntryType): List<Form> {
        return formsRepository.getEntries(monitoring.code, entryType.name)
    }

    suspend fun createNew(): Monitoring {
        val code: String = generateMonitoringCode()
        val monitoring = Monitoring(code)
        monitoring.id = monitoringRepository.createMonitoring(toDbModel(monitoring))
        return monitoring
    }

    suspend fun update(monitoring: Monitoring) {
        monitoringRepository.updateMonitoring(toDbModel(monitoring))
    }

    suspend fun updateStatus(monitoringCode: String, status: Monitoring.Status) {
        monitoringRepository.updateStatus(monitoringCode, status)
    }

    suspend fun updateStatus(monitoring: Monitoring, status: Monitoring.Status) {
        updateStatus(monitoring.code, status)
        monitoring.status = status
    }

    suspend fun getMonitoring(monitoringCode: String): Monitoring? {
        return monitoringFromDb(monitoringRepository.getMonitoring(monitoringCode))
    }

    fun getMonitoringLive(monitoringCode: String): LiveData<Monitoring?> {
        return monitoringRepository.getMonitoringLive(monitoringCode).switchMap {
            liveData {
                emit(monitoringFromDb(it))
            }
        }
    }

    suspend fun getActiveMonitoring(): Monitoring? {
        return monitoringFromDb(monitoringRepository.getActiveMonitoring())
    }

    suspend fun monitoringCodesForStatus(status: Monitoring.Status): Iterable<String> {
        return monitoringRepository.getMonitoringCodesForStatus(status)
    }

    suspend fun getPausedMonitoring(): Monitoring? {
        return monitoringFromDb(monitoringRepository.getPausedMonitoring())
    }

    suspend fun deleteMonitoring(monitoringCode: String?) {
        monitoringRepository.deleteMonitoring(monitoringCode!!)
    }

    suspend fun countMonitoringsForStatus(status: Monitoring.Status): Int {
        return monitoringRepository.countMonitoringsForStatus(status)
    }

    private fun generateMonitoringCode(): String {
        val uuid = UUID.randomUUID().toString()
        return String.format("%s-%s", DATE_FORMATTER.format(Date()), uuid.substring(uuid.length - 12))
    }

    private fun toDbModel(monitoring: Monitoring): MonitoringModel {
        return MonitoringModel(
            monitoring.id,
            monitoring.code,
            monitoring.status.name,
            SERIALIZER.toJson(monitoring).toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun toDbModel(entry: MonitoringEntry): Form {
        return Form(
            entry.id,
            entry.monitoringCode,
            entry.type.name,
            entry.data[tagLatitude]!!.toDouble(),
            entry.data[tagLongitude]!!.toDouble(),
            SERIALIZER.toJson(entry).toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun toDbModel(location: TrackingLocation): Tracking {
        return Tracking(
            location.id,
            location.monitoringCode,
            location.time,
            location.latitude,
            location.longitude,
            location.altitude
        )
    }
}