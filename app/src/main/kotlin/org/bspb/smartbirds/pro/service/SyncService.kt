package org.bspb.smartbirds.pro.service

import android.content.Intent
import kotlinx.coroutines.runBlocking
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.androidannotations.annotations.sharedpreferences.Pref
import org.androidannotations.api.support.app.AbstractIntentService
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.prefs.UserPrefs_
import org.bspb.smartbirds.pro.sync.AppSettingsManager
import org.bspb.smartbirds.pro.sync.AuthenticationManager
import org.bspb.smartbirds.pro.sync.UploadManager
import org.bspb.smartbirds.pro.sync.ZonesManager
import org.bspb.smartbirds.pro.utils.NomenclaturesManager

@EIntentService
open class SyncService : AbstractIntentService("SyncService") {

    companion object {
        private const val TAG = SmartBirdsApplication.TAG + ".SyncService"

        const val ACTION_SYNC_PROGRESS = "syncProgress"
        const val ACTION_SYNC_COMPLETED = "syncCompleted"
        const val EXTRA_SYNC_MESSAGE = "syncMessage"

        var isWorking = false
        var syncMessage: String? = null
    }

    @Pref
    protected lateinit var prefs: UserPrefs_

    @Bean
    protected lateinit var uploadManager: UploadManager

    private val nomenclaturesManager = NomenclaturesManager.getInstance()

    @ServiceAction
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
                sendBroadcast(intent)
            }
        }
    }

    @ServiceAction
    fun initialSync() {
        runBlocking {
            try {
                isWorking = true
                fetchNewData()
            } finally {
                isWorking = false
                syncMessage = null
                val intent = Intent(ACTION_SYNC_COMPLETED)
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
        intent.putExtra(EXTRA_SYNC_MESSAGE, syncMessage)
        sendBroadcast(intent)
    }

}