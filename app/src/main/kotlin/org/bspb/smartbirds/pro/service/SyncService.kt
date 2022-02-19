package org.bspb.smartbirds.pro.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.androidannotations.annotations.sharedpreferences.Pref
import org.androidannotations.api.support.app.AbstractIntentService
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.SmartBirdsApplication
import org.bspb.smartbirds.pro.prefs.UserPrefs_
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
    protected lateinit var authenticationManager: AuthenticationManager

    @Bean
    protected lateinit var zonesManager: ZonesManager

    @Bean
    protected lateinit var uploadManager: UploadManager

    private val nomenclaturesManager = NomenclaturesManager.getInstance()

    @ServiceAction
    fun sync() {
        GlobalScope.launch {
            try {
                isWorking = true
                updateSyncProgress(R.string.upload_dialog_text)
                uploadManager.uploadAll()
                fetchNewData()
            } finally {
                isWorking = false
                syncMessage = null
                val intent = Intent(ACTION_SYNC_COMPLETED)
                LocalBroadcastManager.getInstance(this@SyncService).sendBroadcast(intent)
            }
        }
    }

    @ServiceAction
    fun initialSync() {
        GlobalScope.launch {
            try {
                isWorking = true
                fetchNewData()
            } finally {
                isWorking = false
                syncMessage = null
                val intent = Intent(ACTION_SYNC_COMPLETED)
                LocalBroadcastManager.getInstance(this@SyncService).sendBroadcast(intent)
            }
        }
    }

    private suspend fun fetchNewData() {
        updateSyncProgress(R.string.sync_dialog_downloading_user_data)
        authenticationManager.checkSession();
        updateSyncProgress(R.string.sync_dialog_downloading_nomenclatures)
        nomenclaturesManager.updateNomenclatures()
        updateSyncProgress(R.string.sync_dialog_downloading_zones)
        zonesManager.downloadZones()
    }

    private fun updateSyncProgress(messageResource: Int) {
        syncMessage = getString(messageResource)
        val intent = Intent(ACTION_SYNC_PROGRESS)
        intent.putExtra(EXTRA_SYNC_MESSAGE, syncMessage)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}