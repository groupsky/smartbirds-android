package org.bspb.smartbirds.pro.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.sync.AppSettingsManager
import org.bspb.smartbirds.pro.sync.AuthenticationManager
import org.bspb.smartbirds.pro.sync.UploadManager
import org.bspb.smartbirds.pro.sync.ZonesManager
import org.bspb.smartbirds.pro.utils.NomenclaturesManager

class SyncService : IntentService("SyncService") {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".SyncService"

        const val ACTION_SYNC: String = "sync"
        const val ACTION_INITIAL_SYNC: String = "initialSync"

        const val ACTION_SYNC_PROGRESS = "syncProgress"
        const val ACTION_SYNC_COMPLETED = "syncCompleted"
        const val EXTRA_SYNC_MESSAGE = "syncMessage"

        var isWorking = false
        var syncMessage: String? = null

        fun initialSyncIntent(context: Context): Intent {
            return Intent(context, SyncService::class.java).apply {
                action = ACTION_INITIAL_SYNC
            }
        }

        fun syncIntent(context: Context): Intent {
            return Intent(context, SyncService::class.java).apply {
                action = ACTION_SYNC
            }
        }
    }

    private val uploadManager: UploadManager by lazy { UploadManager(this) }

    private val nomenclaturesManager = NomenclaturesManager.getInstance()

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val action = intent.action
        if (ACTION_SYNC == action) {
            sync()
            return
        }
        if (ACTION_INITIAL_SYNC == action) {
            initialSync()
            return
        }
    }

    fun sync() {
        runBlocking {
            try {
                isWorking = true
                updateSyncProgress(R.string.upload_dialog_text)
                uploadManager.uploadAll()
                fetchNewData()
            } finally {
                isWorking = false
                syncMessage = null
                val intent = Intent(ACTION_SYNC_COMPLETED)
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }
        }
    }

    fun initialSync() {
        runBlocking {
            try {
                isWorking = true
                fetchNewData()
            } finally {
                isWorking = false
                syncMessage = null
                val intent = Intent(ACTION_SYNC_COMPLETED)
                intent.setPackage(packageName)
                sendBroadcast(intent)
            }
        }
    }

    private suspend fun fetchNewData() {
        val zonesManager = ZonesManager(this)
        val appSettingsManager = AppSettingsManager(this)

        updateSyncProgress(R.string.sync_dialog_downloading_user_data)
        val authenticationManager = AuthenticationManager(this)
        authenticationManager.checkSession();
        updateSyncProgress(R.string.sync_dialog_downloading_nomenclatures)
        nomenclaturesManager.updateNomenclatures()
        updateSyncProgress(R.string.sync_dialog_downloading_zones)
        zonesManager.downloadZones()
        updateSyncProgress(R.string.sync_dialog_downloading_app_settings)
        appSettingsManager.fetchSettings()
    }

    private fun updateSyncProgress(messageResource: Int) {
        syncMessage = getString(messageResource)
        val intent = Intent(ACTION_SYNC_PROGRESS)
        intent.setPackage(packageName)
        intent.putExtra(EXTRA_SYNC_MESSAGE, syncMessage)
        sendBroadcast(intent)
    }

}