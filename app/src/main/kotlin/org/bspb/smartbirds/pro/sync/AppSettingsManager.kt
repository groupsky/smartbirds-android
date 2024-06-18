package org.bspb.smartbirds.pro.sync

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import java.io.IOException

@EBean(scope = EBean.Scope.Default)
open class AppSettingsManager {

    companion object {
        var isDownloading = false
    }

    @RootContext
    protected lateinit var context: Context

    protected val backend: Backend by lazy { Backend.getInstance() }

    @Pref
    protected lateinit var prefs: SmartBirdsPrefs_

    open fun fetchSettings() {
        isDownloading = true
        try {
            try {

                val response = backend.api().mapLayers(-1, 0).execute()
                if (!response.isSuccessful) {
                    throw IOException("Server error: " + response.code() + " - " + response.message())
                }
                response.body()?.data?.apply {
                    prefs.mapLayers().put(SBGsonParser.createParser().toJson(this))
                }
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast("Could not fetch app settings. Try again.")
            }
        } finally {
            isDownloading = false
        }
    }

    protected open fun showToast(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context.applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}