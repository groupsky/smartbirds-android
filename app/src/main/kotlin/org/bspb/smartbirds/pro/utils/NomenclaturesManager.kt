package org.bspb.smartbirds.pro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.Backend_
import org.bspb.smartbirds.pro.backend.dto.Nomenclature
import org.bspb.smartbirds.pro.events.EEventBus_
import org.bspb.smartbirds.pro.events.NomenclaturesReadyEvent
import org.bspb.smartbirds.pro.room.NomenclatureModel
import org.bspb.smartbirds.pro.room.NomenclatureUsesCount
import org.bspb.smartbirds.pro.room.SmartBirdsRoomDatabase
import org.bspb.smartbirds.pro.tools.AlphanumComparator
import org.bspb.smartbirds.pro.tools.Reporting
import org.bspb.smartbirds.pro.tools.SBGsonParser
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class NomenclaturesManager private constructor(val context: Context) {

    enum class Downloading {
        LOCATIONS, NOMENCLATURES
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: NomenclaturesManager? = null

        var isDownloading: MutableSet<Downloading> = HashSet()

        fun getInstance(): NomenclaturesManager {
            checkNotNull(INSTANCE) { "NomenclaturesManager instance is null. init(Context context) must be called before getting the instance." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                INSTANCE = NomenclaturesManager(context)
                INSTANCE!!.loadNomenclatures()
                INSTANCE
            }
        }
    }

    private val data = mutableMapOf<String, MutableList<Nomenclature>>()
    private val locale = context.getString(R.string.locale)
    private var loading = false
    private val db = SmartBirdsRoomDatabase.getInstance()
    private val backend: Backend = Backend_.getInstance_(context)

    private val comparator: Comparator<in Nomenclature> =
        Comparator { o1: Nomenclature, o2: Nomenclature ->
            if (o1.label.labelId.equals(o2.label.labelId, ignoreCase = true)) {
                return@Comparator 0
            }
            AlphanumComparator.compareStrings(o1.localeLabel, o2.localeLabel)
        }

    private fun loadNomenclatures() {
        GlobalScope.launch {
            loading = true
            data.clear()
            val dbNomenclatures = db.nomenclatureDao().getAll()
            dbNomenclatures.forEach { nomenclature ->
                nomenclature.data?.let { nomenclatureData ->
                    val list: MutableList<Nomenclature>?
                    if (!data.containsKey(nomenclature.type)) {
                        list = mutableListOf()
                        data[nomenclature.type!!] = list
                    } else {
                        list = data[nomenclature.type]
                    }
                    list!!.add(Nomenclature.fromData(String(nomenclatureData), locale))
                }
            }

            // load any missing nomenclatures from bundled - this is useful when updating, before sync with server
            val missingNomenclatures = mutableSetOf<String>()
            for (species in loadBundledSpecies()) {
                fillMissingNomenclature(
                    missingNomenclatures,
                    localizeNomenclature(Nomenclature.fromSpecies(species, locale), locale)
                )
            }

            missingNomenclatures.clear()
            for (nomenclature in loadBundledNomenclatures()) {
                fillMissingNomenclature(
                    missingNomenclatures,
                    localizeNomenclature(nomenclature, locale)
                )
            }

            // sort nomenclatures
            for (nomenclatures in data.values) {
                Collections.sort(nomenclatures, comparator)
            }

            loading = false
            EEventBus_.getInstance_(context).postSticky(NomenclaturesReadyEvent())
        }
    }

    private fun loadBundledNomenclatures(): Iterable<Nomenclature> {
        return loadBundledFile("nomenclatures.json")
    }

    private fun loadBundledSpecies(): Iterable<Nomenclature> {
        return loadBundledFile("species.json")
    }

    private fun loadBundledFile(filename: String): Iterable<Nomenclature> {
        return try {
            val inStream: InputStream = BufferedInputStream(context.assets.open(filename))
            inStream.use {
                listOf(
                    *SBGsonParser.createParser().fromJson(
                        InputStreamReader(it),
                        Array<Nomenclature>::class.java
                    )
                )
            }
        } catch (e: IOException) {
            Reporting.logException(e)
            throw IllegalStateException("Missing bundled file $filename", e)
        }
    }

    private fun fillMissingNomenclature(
        missingNomenclatures: MutableSet<String>,
        nomenclature: Nomenclature
    ) {
        val list: MutableList<Nomenclature>?
        if (!data.containsKey(nomenclature.type)) {
            missingNomenclatures.add(nomenclature.type)
            list = LinkedList()
            data[nomenclature.type] = list
        } else if (missingNomenclatures.contains(nomenclature.type)) {
            list = data[nomenclature.type]
        } else return
        list!!.add(nomenclature)
    }

    private fun localizeNomenclature(nomenclature: Nomenclature, locale: String): Nomenclature {
        nomenclature.localeLabel = nomenclature.label[locale]
        return nomenclature
    }

    fun getNomenclature(key: String): List<Nomenclature> {
        var key = key
        key = key.replaceFirst("^form_".toRegex(), "")
        if (!data.containsKey(key)) {
            check(!loading) { "Still loading" }
            throw IllegalArgumentException("Unknown nomenclature $key")
        }

        return data[key]!!
    }

    fun isLoading() = loading

    fun getRecentNomenclatures(key: String): LiveData<List<Nomenclature>> {
        return liveData {
//            emit(listOf())

            var type = key.replaceFirst("^form_".toRegex(), "")
            val nomenclatures: List<Nomenclature> = getNomenclature(type)
            val recentItems = mutableListOf<Nomenclature>()

            val dbNomenclatures =
                db.nomenclatureUsesCountDao().findByType(type)
            var idx: Int
            dbNomenclatures.forEach { nomenclature ->
                nomenclature.data?.let { data ->
                    val temp = Nomenclature.fromData(String(data), locale)
                    idx = Collections.binarySearch(nomenclatures, temp, comparator)
                    if (idx >= 0) {
                        recentItems.add(nomenclatures[idx])
                    }
                }
            }
            emit(recentItems)
        }
    }

    fun addRecentNomenclature(nomenclature: Nomenclature) {
        GlobalScope.launch {
            var recentNomenclature =
                db.nomenclatureUsesCountDao().findByLabel(nomenclature.label.labelId)
            if (recentNomenclature == null) {
                recentNomenclature = NomenclatureUsesCount(
                    0,
                    nomenclature.type,
                    nomenclature.label.labelId,
                    1,
                    SBGsonParser.createParser().toJson(nomenclature).toByteArray(Charsets.UTF_8)
                )
                db.nomenclatureUsesCountDao().insert(recentNomenclature)
            } else {
                recentNomenclature.count = recentNomenclature.count?.plus(1)
                db.nomenclatureUsesCountDao().update(recentNomenclature)
            }
        }

    }

    suspend fun updateNomenclatures() {
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

    private fun showToast(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                context.applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}