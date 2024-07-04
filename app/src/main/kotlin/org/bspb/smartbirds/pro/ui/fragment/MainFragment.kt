package org.bspb.smartbirds.pro.ui.fragment

import android.Manifest.permission
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.text.Html
import android.text.Html.ImageGetter
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.content.Monitoring
import org.bspb.smartbirds.pro.events.CancelMonitoringEvent
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.ExportFailedEvent
import org.bspb.smartbirds.pro.events.ExportPreparedEvent
import org.bspb.smartbirds.pro.events.MonitoringCanceledEvent
import org.bspb.smartbirds.pro.events.MonitoringFinishedEvent
import org.bspb.smartbirds.pro.events.MonitoringPausedEvent
import org.bspb.smartbirds.pro.events.ResumeMonitoringEvent
import org.bspb.smartbirds.pro.events.StartMonitoringEvent
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs
import org.bspb.smartbirds.pro.service.DataService
import org.bspb.smartbirds.pro.service.ExportService
import org.bspb.smartbirds.pro.service.SyncService
import org.bspb.smartbirds.pro.sync.UploadManager
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.DownloadsActivity
import org.bspb.smartbirds.pro.ui.MonitoringListActivity
import org.bspb.smartbirds.pro.ui.MonitoringReportActivity
import org.bspb.smartbirds.pro.ui.SettingsActivity
import org.bspb.smartbirds.pro.ui.StatsActivity
import org.bspb.smartbirds.pro.utils.MonitoringManager
import org.bspb.smartbirds.pro.utils.SBScope
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.showAlert
import java.util.Date

class MainFragment : Fragment() {

    private val REQUEST_LOCATION = 0
    private val REQUEST_STORAGE = 1
    private val REQUEST_NOTIFICATION = 2

    private var exportDialog: AlertDialog? = null
    private var loading: LoadingDialog? = null

    private lateinit var btnBatteryOptimization: ImageButton
    private lateinit var btnUpload: Button
    private lateinit var btnStartBirds: Button
    private lateinit var btnResumeBirds: Button
    private lateinit var btnCancelBirds: Button

    private lateinit var prefs: SmartBirdsPrefs

    private val monitoringManager = MonitoringManager.getInstance()
    private val bus: EEventBus by lazy { EEventBus.getInstance() }

    private var lastMonitoring: Monitoring? = null
    private var menuBrowseLastMonitorig: MenuItem? = null

    private val scope = SBScope()

    private val syncBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (action == SyncService.ACTION_SYNC_PROGRESS) {
                    updateSyncProgress(intent.getStringExtra(SyncService.EXTRA_SYNC_MESSAGE))
                } else if (action == SyncService.ACTION_SYNC_COMPLETED) {
                    onSyncComplete()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initPrefs()
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState) ?: inflater.inflate(
            R.layout.fragment_main,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupMonitoringButtons()
    }

    private fun initViews() {
        btnBatteryOptimization = requireView().findViewById(R.id.btn_battery_optimization)
        btnUpload = requireView().findViewById(R.id.btn_upload)
        btnStartBirds = requireView().findViewById(R.id.btn_start_birds)
        btnResumeBirds = requireView().findViewById(R.id.btn_resume_birds)
        btnCancelBirds = requireView().findViewById(R.id.btn_cancel_birds)

        btnStartBirds.setOnClickListener { startBirdsClicked() }
        btnResumeBirds.setOnClickListener { resumeBirdsClicked() }
        btnCancelBirds.setOnClickListener { cancelBirdsClicked() }
        btnUpload.setOnClickListener { uploadBtnClicked() }
        btnBatteryOptimization.setOnClickListener { showBatteryOptimizationDialog() }
    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter()
        intentFilter.addAction(SyncService.ACTION_SYNC_COMPLETED)
        intentFilter.addAction(SyncService.ACTION_SYNC_PROGRESS)
        ContextCompat.registerReceiver(
            requireActivity(),
            syncBroadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        bus.registerSticky(this)
        if (SyncService.isWorking) {
            updateSyncProgress(SyncService.syncMessage)
        }

        // Observe Not synced monitorings count
        monitoringManager.countMonitoringsForStatusLive(Monitoring.Status.finished).observe(this) {
            displayNotSyncedCount(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!SyncService.isWorking) {
            onSyncComplete()
        }
        checkBatteryOptimization()
        checkForLastMonitoring()
    }

    override fun onStop() {
        bus.unregister(this)
        requireActivity().unregisterReceiver(syncBroadcastReceiver)
        super.onStop()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION, REQUEST_STORAGE -> {
                var granted = true
                for (grantResult in grantResults) if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
                if (!granted) {
                    Toast.makeText(activity, R.string.permissions_required, Toast.LENGTH_SHORT)
                        .show()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && REQUEST_LOCATION == requestCode && granted) {
                    requestPermissions(
                        arrayOf(permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_LOCATION
                    )
                }
            }
        }
    }

    private fun initPrefs() {
        prefs = SmartBirdsPrefs(requireContext())
    }

    private fun setupMonitoringButtons() {
        if (prefs.getPausedMonitoring()) {
            btnStartBirds.visibility = View.GONE
            btnResumeBirds.visibility = View.VISIBLE
            btnCancelBirds.visibility = View.VISIBLE
        } else {
            btnStartBirds.visibility = View.VISIBLE
            btnResumeBirds.visibility = View.GONE
            btnCancelBirds.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        menuBrowseLastMonitorig = menu.findItem(R.id.menu_report_last)
        checkForLastMonitoring()
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.menu_export) {
            exportBtnClicked()
            return true
        }
        if (itemId == R.id.menu_browse) {
            browseBtnClicked()
            return true
        }
        if (itemId == R.id.menu_report_last) {
            reportBtnClicked()
            return true
        }
        if (itemId == R.id.menu_help) {
            helpBtnClicked()
            return true
        }
        if (itemId == R.id.menu_information) {
            infoBtnClicked()
            return true
        }
        if (itemId == R.id.menu_downloads) {
            openDownloads()
            return true
        }
        if (itemId == R.id.menu_statistics) {
            showStats()
            return true
        }
        if (itemId == R.id.menu_settings) {
            showSettings()
            return true
        }
        if (itemId == R.id.menu_privacy_policy) {
            openPrivacyPolicy()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkForLastMonitoring() {
        lifecycleScope.launch {
            lastMonitoring = monitoringManager.getLastMonitoring()
            menuBrowseLastMonitorig?.isEnabled = lastMonitoring != null
        }
    }

    private fun startBirdsClicked() {
        if (!permissionsGranted()) return
        bus.postSticky(StartMonitoringEvent())
    }

    private fun resumeBirdsClicked() {
        if (!permissionsGranted()) return
        bus.postSticky(ResumeMonitoringEvent())
    }

    private fun cancelBirdsClicked() {
        confirmCancel()
    }

    private fun uploadBtnClicked() {
        activity?.let {
            it.startService(SyncService.syncIntent(it))
        }
    }

    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.battery_optimization_title))
            .setMessage(getString(R.string.battery_optimization_message))
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun exportBtnClicked() {
        exportDialog = ProgressDialog.show(
            activity,
            getString(R.string.export_dialog_title),
            getString(R.string.export_dialog_text),
            true
        )
        activity?.let {
            it.startService(ExportService.newIntent(it))
        }
    }

    private fun browseBtnClicked() {
        startActivity(Intent(activity, MonitoringListActivity::class.java))
    }

    private fun reportBtnClicked() {
        if (lastMonitoring == null) {
            requireContext().showAlert(
                R.string.report_last_monitoring_warning_title,
                R.string.report_last_monitoring_warning_message
            )
        } else {
            startActivity(
                MonitoringReportActivity.newIntent(
                    requireContext(),
                    lastMonitoring!!.code
                )
            )
        }
    }

    private fun helpBtnClicked() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.help_url))
        context?.packageManager?.let {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                debugLog("helpButton exception: $e")
            }
        }
    }

    private fun infoBtnClicked() {
        val density = resources.displayMetrics.density
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.info_dialog_title))
        val dialogView = layoutInflater.inflate(R.layout.dialog_information, null, false)

        val textView: TextView = dialogView.findViewById(R.id.info_text)
        textView.text = Html.fromHtml(getString(R.string.info_text), ImageGetter { s ->
            var drawable: Drawable? = null
            when (s) {
                "logo_bspb" -> drawable = resources.getDrawable(R.drawable.logo_bspb)
                "logo_mtel" -> drawable = resources.getDrawable(R.drawable.logo_mtel)
                "life_NEW" -> drawable = resources.getDrawable(R.drawable.logo_life)
                "natura2000_NEW" -> drawable = resources.getDrawable(R.drawable.logo_natura_2000)
            }
            if (drawable == null) {
                Reporting.logException(IllegalArgumentException("Unknown image: $s"))
            } else {
                drawable.setBounds(
                    0, 0, (drawable.intrinsicWidth * density).toInt(),
                    (drawable.intrinsicHeight * density).toInt()
                )
            }
            drawable!!
        }, null)

        builder.setView(dialogView)
        builder.create().show()
    }

    private fun openDownloads() {
        startActivity(Intent(activity, DownloadsActivity::class.java))
    }

    private fun showStats() {
        startActivity(Intent(activity, StatsActivity::class.java))
    }

    private fun showSettings() {
        startActivity(Intent(activity, SettingsActivity::class.java))
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(getString(R.string.privacy_policy_url))
        context?.packageManager?.let {
            try {
                startActivity(intent)
            } catch (e: Exception) {
                debugLog("openPrivacyPolicy exception: $e")
            }
        }
    }

    //    @LongClick(R.id.btn_export)
    private fun displayDescription(v: View): Boolean {
        if (TextUtils.isEmpty(v.contentDescription)) return false
        val screenPos = IntArray(2)
        val displayFrame = Rect()
        v.getLocationOnScreen(screenPos)
        v.getWindowVisibleDisplayFrame(displayFrame)
        val context = v.context
        val width = v.width
        val height = v.height
        val midy = screenPos[1] + height / 2
        var referenceX = screenPos[0] + width / 2
        if (ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            val screenWidth = context.resources.displayMetrics.widthPixels
            referenceX = screenWidth - referenceX // mirror
        }
        val cheatSheet = Toast.makeText(context, v.contentDescription, Toast.LENGTH_SHORT)
        if (midy < displayFrame.height()) {
            // Show along the top; follow action buttons
            cheatSheet.setGravity(Gravity.TOP or GravityCompat.END, referenceX, height)
        } else {
            // Show along the bottom center
            cheatSheet.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, height)
        }
        cheatSheet.show()
        return true
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                btnBatteryOptimization.visibility = View.GONE
            } else {
                if (!prefs.getBatteryOptimizationDialogShown()) {
                    showBatteryOptimizationDialog()
                    prefs.setBatteryOptimizationDialogShown(true)
                }
                btnBatteryOptimization.visibility = View.VISIBLE
            }
        } else {
            btnBatteryOptimization.visibility = View.GONE
        }
    }

    private fun displayNotSyncedCount(notSyncedCount: Int) {
        scope.launch(Dispatchers.Main) {
            try {
                btnUpload.text = "${getString(R.string.main_screen_btn_upload)} : $notSyncedCount"
            } catch (t: Throwable) {
                // IllegalStateException: not attached to Activity
                Reporting.logException(t)
            }
        }

    }

    private fun updateSyncProgress(message: String?) {
        scope.launch(Dispatchers.Main) {
            showProgressDialog(message ?: "")
        }
    }

    private fun onSyncComplete() {
        scope.launch(Dispatchers.Main) {
            hideProgressDialog()
            showErrorsIfAny()
        }
    }

    private fun showErrorsIfAny() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return
        }
        if (UploadManager.errors.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append(getString(R.string.sync_error_general_message))
            sb.append("<br /><br />")
            sb.append("<strong>${getString(R.string.sync_error_details)}</strong><br />")
            sb.append("<ul>")
            sb.append(UploadManager.errors.joinToString("") { "<li>$it</li>" })
            sb.append("</ul>")
            context?.showAlert(
                getString(R.string.sync_dialog_error_title),
                HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_COMPACT),
                null,
                null
            )
        }
    }

    fun onEvent(event: ExportPreparedEvent) {
        scope.launch(Dispatchers.Main) {
            exportDialog?.cancel()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/zip"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.export_text, Date().toString()))
            intent.putExtra(Intent.EXTRA_STREAM, event.uri)
            startActivity(Intent.createChooser(intent, getString(R.string.export_app_chooser)))
        }
    }

    fun onEvent(event: ExportFailedEvent?) {
        scope.launch(Dispatchers.Main) {
            exportDialog?.cancel()
            Toast.makeText(activity, getString(R.string.export_failed_error), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun onEvent(event: MonitoringPausedEvent?) {
        scope.launch(Dispatchers.Main) {
            setupMonitoringButtons()
            bus.removeStickyEvent(MonitoringPausedEvent::class.java)
        }
    }

    fun onEvent(event: MonitoringCanceledEvent?) {
        scope.launch(Dispatchers.Main) {
            setupMonitoringButtons()
            checkForLastMonitoring()
            bus.removeStickyEvent(MonitoringCanceledEvent::class.java)
        }
    }

    fun onEvent(event: MonitoringFinishedEvent?) {
        scope.launch(Dispatchers.Main) {
            setupMonitoringButtons()
            bus.removeStickyEvent(MonitoringFinishedEvent::class.java)
        }
    }

    private fun permissionsGranted(): Boolean {
        return locationPermissionsGranted() && storagePermissionsGranted() && notificationPermissionsGranted()
    }

    private fun locationPermissionsGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        val permissions =
            mutableListOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)

        context?.showAlert(
            R.string.location_permission_alert_title,
            R.string.location_permission_alert_message,
            { _, _ ->
                requestPermissions(permissions.toTypedArray(), REQUEST_LOCATION)
            },
            null
        )

        return false
    }

    private fun storagePermissionsGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        if (shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                Snackbar.make(
                    btnStartBirds,
                    R.string.storage_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        requestPermissions(
                            arrayOf(
                                permission.WRITE_EXTERNAL_STORAGE,
                                permission.READ_EXTERNAL_STORAGE
                            ), REQUEST_STORAGE
                        )
                    }.show()
                return false
            } catch (t: Throwable) {
                // we get IAE because we don't extend the Theme.AppCompat, but that messes up styling of the fields
                Reporting.logException(t)
            }
        }
        requestPermissions(
            arrayOf(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE
            ), REQUEST_STORAGE
        )
        Toast.makeText(activity, R.string.storage_permission_rationale, Toast.LENGTH_SHORT).show()
        return false
    }

    private fun notificationPermissionsGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }
        if (shouldShowRequestPermissionRationale(permission.POST_NOTIFICATIONS)) {
            try {
                Snackbar.make(
                    btnStartBirds,
                    R.string.notifications_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        requestPermissions(
                            arrayOf(
                                permission.POST_NOTIFICATIONS
                            ), REQUEST_NOTIFICATION
                        )
                    }.show()
                return false
            } catch (t: Throwable) {
                // we get IAE because we don't extend the Theme.AppCompat, but that messes up styling of the fields
                Reporting.logException(t)
            }
        }
        requestPermissions(
            arrayOf(
                permission.POST_NOTIFICATIONS
            ), REQUEST_NOTIFICATION
        )
        Toast.makeText(activity, R.string.notifications_permission_rationale, Toast.LENGTH_SHORT)
            .show()
        return false
    }

    private fun confirmCancel() {
        //Ask the user if they want to quit
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_alert)
            .setTitle(R.string.cancel_monitoring)
            .setMessage(R.string.really_cancel_monitoring)
            .setPositiveButton(android.R.string.yes) { _, _ -> doCancel() }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun doCancel() {
        context?.startService(DataService.intent(context))
        bus.postSticky(CancelMonitoringEvent())
    }

    private fun showProgressDialog(message: String) {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return
        }

        if (loading == null) {
            loading = parentFragmentManager.findFragmentByTag("progress") as LoadingDialog?
        }

        if (loading != null) {
            loading!!.updateTexts(message)
            return
        }


        val loadingDialog: LoadingDialog = LoadingDialog.newInstance(message)
        loadingDialog.show(parentFragmentManager, "progress")
        loading = loadingDialog
    }

    private fun hideProgressDialog() {
        loading?.dismiss()
        loading = null
    }


}