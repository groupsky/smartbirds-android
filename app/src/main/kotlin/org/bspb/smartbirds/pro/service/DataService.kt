package org.bspb.smartbirds.pro.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.ActiveMonitoringEvent
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent
import org.bspb.smartbirds.pro.events.CreateImageFile
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.EntrySubmitted
import org.bspb.smartbirds.pro.events.FinishMonitoringEvent
import org.bspb.smartbirds.pro.events.GetImageFile
import org.bspb.smartbirds.pro.events.GetMonitoringCommonData
import org.bspb.smartbirds.pro.events.ImageFileCreated
import org.bspb.smartbirds.pro.events.ImageFileCreatedFailed
import org.bspb.smartbirds.pro.events.ImageFileEvent
import org.bspb.smartbirds.pro.events.MonitoringCanceledEvent
import org.bspb.smartbirds.pro.events.MonitoringCommonData
import org.bspb.smartbirds.pro.events.MonitoringFailedEvent
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.events.MonitoringPausedEvent
import org.bspb.smartbirds.pro.events.MonitoringResumedEvent
import org.bspb.smartbirds.pro.events.MonitoringStartedEvent
import org.bspb.smartbirds.pro.events.PauseMonitoringEvent
import org.bspb.smartbirds.pro.events.QueryActiveMonitoringEvent
import org.bspb.smartbirds.pro.events.ResumeMonitoringEvent
import org.bspb.smartbirds.pro.events.SetMonitoringCommonData
import org.bspb.smartbirds.pro.events.StartMonitoringEvent
import org.bspb.smartbirds.pro.events.SubmitFishesCommonForm
import org.bspb.smartbirds.pro.events.UndoLastEntry
import org.bspb.smartbirds.pro.events.UserDataEvent
import org.bspb.smartbirds.pro.prefs.MonitoringPrefs
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs
import org.bspb.smartbirds.pro.prefs.UserPrefs
import org.bspb.smartbirds.pro.tools.GpxWriter
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.utils.Configuration
import org.bspb.smartbirds.pro.ui.utils.NotificationUtils
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.utils.MonitoringUtils
import org.bspb.smartbirds.pro.utils.MonitoringUtils.Companion.closeGpxFile
import org.bspb.smartbirds.pro.utils.MonitoringUtils.Companion.createMonitoringDir
import org.bspb.smartbirds.pro.utils.MonitoringUtils.Companion.initGpxFile
import org.bspb.smartbirds.pro.utils.SBScope
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.CancellationException

open class DataService : Service() {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".DataService"
        private val DATE_FORMATTER = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        private val GPX_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

        init {
            DATE_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
            GPX_DATE_FORMATTER.timeZone = TimeZone.getTimeZone("UTC")

        }

        fun intent(context: Context?): Intent {
            return Intent(context, DataService::class.java)
        }
    }

    private var initServiceJob: Job? = null
    private val binder: IBinder = Binder()

    private val scope = SBScope()

    protected val bus: EEventBus by lazy { EEventBus.getInstance() }
    private lateinit var globalPrefs: SmartBirdsPrefs
    private lateinit var userPrefs: UserPrefs
    private lateinit var monitoringPrefs: MonitoringPrefs
    private val monitoringManager = MonitoringManager.getInstance()

    var monitoring: Monitoring? = null
        set(value) {
            field = value
            if (value != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, TrackingService::class.java))
                } else {
                    startService(Intent(this, TrackingService::class.java))
                    NotificationUtils.showMonitoringNotification(applicationContext)
                }
                globalPrefs.setRunningMonitoring(true)
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    NotificationUtils.hideMonitoringNotification(applicationContext)
                }
                stopService(Intent(this, TrackingService::class.java))
                globalPrefs.setRunningMonitoring(false)
            }
        }

    private fun isMonitoring(): Boolean {
        return monitoring != null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        init()
        super.onCreate()
    }

    private fun init() {
        globalPrefs = SmartBirdsPrefs(this)
        userPrefs = UserPrefs(this)
        monitoringPrefs = MonitoringPrefs(this)

        initBus()
    }

    protected open fun initBus() {
        initServiceJob = scope.sbLaunch {
            // restore state
            monitoring = monitoringManager.getActiveMonitoring()

            if (bus.isRegistered(this@DataService))
                Log.d(TAG, "bus already registered")

            Log.d(TAG, "bus registering...")
            bus.registerSticky(this@DataService)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "destroying...")

        initServiceJob?.let {
            if (it.isActive) {
                Log.d(TAG, "canceling initService job...")
                it.cancel(CancellationException("Service destroyed"))
            }
        }

        // Make sure we will unregister from the bus if we were registered during the cancellation
        scope.sbLaunch { bus.unregister(this@DataService) }

        bus.unregister(this)
        // Restart service only if the device is with SDK lower than Oreo, otherwise there is a crash
        // when the service is killed and recreated. The reason is that in Oreo there are
        // limitations for starting services when the app is in background.
        // Restart service only if the device is with SDK lower than Oreo, otherwise there is a crash
        // when the service is killed and recreated. The reason is that in Oreo there are
        // limitations for starting services when the app is in background.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (isMonitoring()) startService(intent(this))
        }

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand...")
        // Start sticky only if the device is with SDK lower than Oreo, otherwise there is a crash
        // when the service is killed and recreated. The reason is that in Oreo there are
        // limitations for starting services when the app is in background.

        // Start sticky only if the device is with SDK lower than Oreo, otherwise there is a crash
        // when the service is killed and recreated. The reason is that in Oreo there are
        // limitations for starting services when the app is in background.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            START_NOT_STICKY
        } else {
            START_STICKY
        }
    }

    open fun onEvent(event: StartMonitoringEvent?) {
        scope.sbLaunch {
            if (isMonitoring()) {
                bus.postSticky(MonitoringStartedEvent())
                return@sbLaunch
            }
            Log.d(TAG, "onStartMonitoringEvent...")
            Toast.makeText(this@DataService, "Start monitoring", Toast.LENGTH_SHORT).show()
            monitoring = monitoringManager.createNew()
            if (createMonitoringDir(this@DataService, monitoring!!) != null && initGpxFile(
                    this@DataService,
                    monitoring!!
                )
            ) {
                bus.postSticky(MonitoringStartedEvent())
            } else {
                Toast.makeText(
                    this@DataService,
                    getString(R.string.error_message_create_monitoring_dir),
                    Toast.LENGTH_SHORT
                ).show()
                bus.post(MonitoringFailedEvent())
            }
        }
    }

    fun onEvent(event: CancelMonitoringEvent) {
        scope.sbLaunch(Dispatchers.Main) {
            Log.d(TAG, "onCancelMonitoringEvent...")
            if (isMonitoring()) {
                monitoringManager.updateStatus(monitoring!!, Monitoring.Status.canceled)
            } else {
                val pausedMonitoring = monitoringManager.getPausedMonitoring()
                if (pausedMonitoring != null) {
                    monitoringManager.updateStatus(pausedMonitoring, Monitoring.Status.canceled)
                }
            }
            globalPrefs.setPausedMonitoring(false)
            monitoring = null
            monitoringPrefs.edit().clear().apply()
            getSharedPreferences(SmartBirdsApplication.PREFS_MONITORING_POINTS, MODE_PRIVATE).edit()
                .clear().apply()
            bus.postSticky(MonitoringCanceledEvent())
            Toast.makeText(
                this@DataService,
                getString(R.string.toast_cancel_monitoring),
                Toast.LENGTH_SHORT
            ).show()
            bus.removeStickyEvent(event)
            stopSelf()
        }
    }

    fun onEvent(event: SetMonitoringCommonData) {
        scope.sbLaunch {
            Log.d(TAG, "onSetMonitoringCommonData")
            event.data[resources.getString(R.string.monitoring_id)] = monitoring!!.code
            event.data[resources.getString(R.string.version)] = Configuration.STORAGE_VERSION_CODE
            monitoring!!.commonForm.clear()
            monitoring!!.commonForm.putAll(event.data)
            monitoringManager.update(monitoring!!)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: FinishMonitoringEvent) {
        scope.sbLaunch {
            if (monitoring!!.commonForm.containsKey(resources.getString(R.string.end_time_key))) {
                if (TextUtils.isEmpty(monitoring!!.commonForm[resources.getString(R.string.end_time_key)])) {
                    monitoring!!.commonForm[resources.getString(R.string.end_time_key)] =
                        Configuration.STORAGE_TIME_FORMAT.format(
                            Date()
                        )
                    monitoringManager.update(monitoring!!)
                }
            }
            monitoringManager.updateStatus(monitoring!!, Monitoring.Status.finished)
            if (isMonitoring()) {
                closeGpxFile(this@DataService, monitoring!!)
            }
            monitoring = null
            bus.postSticky(MonitoringFinishedEvent())
        }
    }

    fun onEvent(event: EntrySubmitted) {
        scope.sbLaunch {
            Log.d(TAG, "onEntrySubmitted")
            try {
                if (event.entryId > 0) {
                    monitoringManager.updateEntry(
                        event.monitoringCode,
                        event.entryId,
                        event.entryType,
                        event.data
                    )
                } else {
                    monitoringManager.newEntry(monitoring!!, event.entryType, event.data)
                }
            } catch (t: Throwable) {
                Reporting.logException("Unable to persist entry", t)
                Toast.makeText(
                    this@DataService,
                    "Could not persist monitoring entry!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onEvent(event: SubmitFishesCommonForm) {
        scope.sbLaunch {
            Log.d(TAG, "onSubmitFishCommonForm")
            try {
                if (!TextUtils.isEmpty(event.monitoringCode)) {
                    monitoringManager.getMonitoring(event.monitoringCode.toString())?.let {
                        it.commonForm.putAll(event.data!!)
                        monitoringManager.update(it)
                    }
                } else {
                    monitoring?.let {
                        it.commonForm.putAll(event.data!!)
                        monitoringManager.update(it)
                    }
                }
            } catch (t: Throwable) {
                Reporting.logException("Unable to persist fish common data", t)
                Toast.makeText(
                    this@DataService,
                    "Could not persist fish common data!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onEvent(location: Location?) {
        Log.d(TAG, "onLocation")
        if (isMonitoring() && location != null) {
            val trackingLocation = monitoringManager.newTracking(monitoring!!, location)
            val file = File(createMonitoringDir(this, monitoring!!), "track.gpx")
            try {
                val osw: Writer = BufferedWriter(FileWriter(file, true))
                osw.use {
                    GpxWriter(it).writePosition(trackingLocation)
                }
            } catch (e: IOException) {
                Reporting.logException(e)
                Toast.makeText(this, "Could not write to track.gpx!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onEvent(event: CreateImageFile) {
        scope.sbLaunch {

            val monitoring =
                if (TextUtils.isEmpty(event.monitoringCode) || isMonitoring() && TextUtils.equals(
                        event.monitoringCode,
                        monitoring!!.code
                    )
                ) monitoring else monitoringManager.getMonitoring(event.monitoringCode)
            // Create an image file name
            var cnt = 100
            while (isMonitoring() && cnt-- > 0) {
                monitoringManager.update(monitoring!!)

                var index = (monitoring.pictureCounter++).toString()
                while (index.length < 4) index = "0$index"
                val imageFileName = "Pic$index.jpg"
                val image = File(createMonitoringDir(this@DataService, monitoring), imageFileName)
                try {
                    if (image.createNewFile()) {
                        val uri =
                            FileProvider.getUriForFile(
                                applicationContext,
                                SmartBirdsApplication.FILES_AUTHORITY,
                                image
                            )
                        bus.post(
                            ImageFileCreated(
                                monitoring.code,
                                imageFileName,
                                uri,
                                image.absolutePath,
                                event.action
                            )
                        )
                        return@sbLaunch
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "Image file create error", e)
                    Reporting.logException(e)
                }
            }
            bus.post(ImageFileCreatedFailed(if (isMonitoring()) monitoring!!.code else null))
        }
    }

    fun onEvent(event: GetImageFile) {
        val image = File(
            MonitoringUtils.getMonitoringDir(
                this,
                if (TextUtils.isEmpty(event.monitoringCode)) monitoring!!.code else event.monitoringCode
            ), event.fileName
        )
        bus.post(
            ImageFileEvent(
                if (TextUtils.isEmpty(event.monitoringCode)) monitoring!!.code else event.monitoringCode,
                event.fileName,
                Uri.fromFile(image),
                image.absolutePath
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: GetMonitoringCommonData) {
        bus.post(MonitoringCommonData(monitoring!!.commonForm))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: UndoLastEntry) {
        scope.sbLaunch {
            monitoringManager.deleteLastEntry(monitoring!!)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: QueryActiveMonitoringEvent) {
        bus.postSticky(ActiveMonitoringEvent(monitoring))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: PauseMonitoringEvent?) {
        scope.sbLaunch {
            if (isMonitoring()) {
                monitoringManager.updateStatus(monitoring!!, Monitoring.Status.paused)
                this@DataService.monitoring = null
            }
            globalPrefs.setPausedMonitoring(true)
            bus.postSticky(MonitoringPausedEvent())
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: ResumeMonitoringEvent) {
        scope.sbLaunch {
            if (isMonitoring()) {
                bus.postSticky(MonitoringResumedEvent())
                return@sbLaunch
            }
            globalPrefs.setPausedMonitoring(false)

            val pausedMonitoring = monitoringManager.getPausedMonitoring()
            if (pausedMonitoring != null) {
                monitoring = pausedMonitoring
                monitoringManager.updateStatus(monitoring!!, Monitoring.Status.wip)
            } else {
                Reporting.logException(IllegalStateException("Trying to resume missing monitoring"))
                monitoring = monitoringManager.createNew()
            }
            bus.postSticky(MonitoringResumedEvent())
        }
    }

    fun onEvent(event: UserDataEvent) {
        if (event.user != null) {
            userPrefs.setUserId(event.user.id)
            userPrefs.setFirstName(event.user.firstName)
            userPrefs.setLastName(event.user.lastName)
            userPrefs.setEmail(event.user.email)
            userPrefs.setBgAtlasCells(SBGsonParser.createParser().toJson(event.user.bgAtlasCells))
        }
        bus.removeStickyEvent(event)
    }
}