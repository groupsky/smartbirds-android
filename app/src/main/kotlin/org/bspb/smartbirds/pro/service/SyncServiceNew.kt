package org.bspb.smartbirds.pro.service

import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EIntentService
import org.androidannotations.annotations.ServiceAction
import org.androidannotations.annotations.sharedpreferences.Pref
import org.androidannotations.api.support.app.AbstractIntentService
import org.bspb.smartbirds.pro.prefs.UserPrefs_
import org.bspb.smartbirds.pro.sync.*

@EIntentService
open class SyncServiceNew : AbstractIntentService("SyncService") {


    @Pref
    protected lateinit var prefs: UserPrefs_

    @Bean
    protected lateinit var authenticationManager: AuthenticationManager

    @Bean
    protected lateinit var nomenclaturesManager: NomenclaturesManager

    @Bean
    protected lateinit var zonesManager: ZonesManager

    @ServiceAction
    fun sync(tag: Long) {
        UploadService_.intent(this).uploadAll(tag).start()
        initialSync()
    }

    @ServiceAction
    fun initialSync() {
        authenticationManager.checkSession();
        nomenclaturesManager.updateNomenclatures(this)
        nomenclaturesManager.downloadLocations(this)
        zonesManager.downloadZones(this)
    }

}