package org.bspb.smartbirds.pro.sync

import android.content.ContentProviderOperation
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.androidannotations.annotations.Bean
import org.androidannotations.annotations.EBean
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.db.SmartBirdsProvider
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Locations
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Nomenclatures
import org.bspb.smartbirds.pro.events.DownloadCompleted
import org.bspb.smartbirds.pro.events.EEventBus
import org.bspb.smartbirds.pro.events.StartingDownload
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.ui.utils.NomenclaturesBean
import java.io.IOException
import java.util.*

@EBean(scope = EBean.Scope.Singleton)
open class NomenclaturesManager {

    enum class Downloading {
        LOCATIONS, NOMENCLATURES
    }

    companion object {
        var isDownloading: MutableSet<Downloading> = HashSet()
    }

    @Bean
    protected lateinit var backend: Backend

    @Bean
    protected lateinit var bus: EEventBus

    @Bean
    protected lateinit var nomenclaturesBean: NomenclaturesBean


    fun downloadLocations(context: Context) {
        isDownloading.add(Downloading.LOCATIONS)
        try {
            bus.post(StartingDownload())
            try {
                val buffer = ArrayList<ContentProviderOperation>()
                buffer.add(ContentProviderOperation.newDelete(Locations.CONTENT_URI).build())
                val response = backend.api().listLocations().execute()
                if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                for (location in response.body()!!.data) {
                    buffer.add(ContentProviderOperation
                            .newInsert(Locations.CONTENT_URI)
                            .withValues(location.toCV())
                            .build())
                }
                context.contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, buffer)
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast(context, "Could not download locations. Try again.")
            }
        } finally {
            isDownloading.remove(Downloading.LOCATIONS)
            if (isDownloading.isEmpty()) {
                bus.post(DownloadCompleted())
            }
        }
    }

    fun updateNomenclatures(context: Context) {
        isDownloading.add(Downloading.NOMENCLATURES)
        try {
            bus.post(StartingDownload())
            try {
                var limit = 500
                var offset = 0
                val buffer = ArrayList<ContentProviderOperation>()
                while (true) {
                    val response = backend.api().nomenclatures(limit, offset).execute()
                    if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                    if (response.body()!!.data.isEmpty()) break
                    offset += nomenclaturesBean.prepareNomenclatureCPO(response.body()!!.data, buffer)
                }

                // if we received nomenclatures
                if (buffer.isNotEmpty()) {
                    buffer.add(0, ContentProviderOperation.newDelete(Nomenclatures.CONTENT_URI).build())
                    context.contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, buffer)
                    buffer.clear()
                    limit = 500
                    offset = 0
                    while (true) {
                        val response = backend.api().species(limit, offset).execute()
                        if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                        if (response.body()!!.data.isEmpty()) break
                        offset += nomenclaturesBean.prepareSpeciesCPO(response.body()!!.data, buffer)
                    }

                    // if we received nomenclatures
                    if (buffer.isNotEmpty()) {
                        context.contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, buffer)
                        buffer.clear()
                    }
                }
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast(context, "Could not download nomenclatures. Try again.")
            }
        } finally {
            isDownloading.remove(Downloading.NOMENCLATURES)
            if (isDownloading.isEmpty()) {
                bus.post(DownloadCompleted())
            }
        }
    }

    protected open fun showToast(context: Context, message: String?) {
        Handler(Looper.getMainLooper()).post { Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show() }
    }
}