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
import org.bspb.smartbirds.pro.db.SmartBirdsProvider.Locations
import org.bspb.smartbirds.pro.room.NomenclatureModel
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase
import org.bspb.smartbirds.pro.tools.Reporting
import java.io.IOException
import java.util.*

@EBean(scope = EBean.Scope.Default)
open class NomenclaturesManager {

    enum class Downloading {
        LOCATIONS, NOMENCLATURES
    }

    companion object {
        var isDownloading: MutableSet<Downloading> = HashSet()
    }

    @RootContext
    protected lateinit var context: Context

    @Bean
    protected lateinit var backend: Backend

    fun downloadLocations() {
        isDownloading.add(Downloading.LOCATIONS)
        try {
            try {
                val buffer = ArrayList<ContentProviderOperation>()
                buffer.add(ContentProviderOperation.newDelete(Locations.CONTENT_URI).build())
                val response = backend.api().listLocations().execute()
                if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                for (location in response.body()!!.data) {
                    buffer.add(
                        ContentProviderOperation
                            .newInsert(Locations.CONTENT_URI)
                            .withValues(location.toCV())
                            .build()
                    )
                }
                context.contentResolver.applyBatch(SmartBirdsProvider.AUTHORITY, buffer)
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast("Could not download locations. Try again.")
            }
        } finally {
            isDownloading.remove(Downloading.LOCATIONS)
        }
    }

    fun updateNomenclatures() {
        isDownloading.add(Downloading.NOMENCLATURES)
        try {
            try {
                var limit = 500
                var offset = 0
                val nomenclatures = mutableListOf<NomenclatureModel>()

                while (true) {
                    val response = backend.api().nomenclatures(limit, offset).execute()
                    if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                    if (response.body()!!.data.isEmpty()) break
                    offset += response.body()!!.data.size
                    response.body()!!.data.forEach {
                        nomenclatures.add(it.convertToEntity())
                    }
                }

                if (nomenclatures.isNotEmpty()) {
                    limit = 500
                    offset = 0
                    while (true) {
                        val response = backend.api().species(limit, offset).execute()
                        if (!response.isSuccessful) throw IOException("Server error: " + response.code() + " - " + response.message())
                        if (response.body()!!.data.isEmpty()) break
                        offset += response.body()!!.data.size
                        response.body()!!.data.forEach {
                            nomenclatures.add(it.convertSpeciesToEntity(context))
                        }
                    }

                    SmartBirdsRoomDatabase.getInstance().nomenclatureDao()
                        .updateNomenclaturesAndClearOld(nomenclatures)
                }
            } catch (t: Throwable) {
                Reporting.logException(t)
                showToast("Could not download nomenclatures. Try again.")
            }
        } finally {
            isDownloading.remove(Downloading.NOMENCLATURES)
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