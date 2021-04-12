package org.bspb.smartbirds.pro.sync

import android.content.ContentProviderOperation
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.db.SmartBirdsProvider
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Zones
import org.bspb.smartbirds.pro.tools.Reporting
import java.io.IOException
import java.util.*

@EBean(scope = EBean.Scope.Default)
open class ZonesManager {

    companion object {
        var isDownloading = false
    }

    @RootContext
    protected lateinit var context: Context

    @Bean
    protected lateinit var backend: Backend

    open fun downloadZones() {
        isDownloading = true
        try {
            try {
                val buffer = ArrayList<ContentProviderOperation>()
                buffer.add(ContentProviderOperation.newDelete(Zones.CONTENT_URI).build())
                val response = backend.api().listZones().execute()
                if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                for (zone in response.body()!!.data) {
                    buffer.add(ContentProviderOperation
                            .newInsert(Zones.CONTENT_URI)
                            .withValues(zone.toCV())
                            .build())
                }
                context.contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, buffer)
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast("Could not download zones. Try again.")
            }
        } finally {
            isDownloading = false
        }
    }

    protected open fun showToast(message: String?) {
        Handler(Looper.getMainLooper()).post { Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

}