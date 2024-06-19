package org.bspb.smartbirds.pro.beans

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.collections.Converter
import org.bspb.smartbirds.pro.content.MonitoringEntry
import org.bspb.smartbirds.pro.enums.EntryType
import org.bspb.smartbirds.pro.ui.map.EntryMapMarker
import org.bspb.smartbirds.pro.ui.utils.FormsConfig

open class EntriesToMapMarkersConverter private constructor() :
    Converter<MonitoringEntry, EntryMapMarker> {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: EntriesToMapMarkersConverter? = null

        fun getInstance(): EntriesToMapMarkersConverter {
            checkNotNull(INSTANCE) { "EntriesToMapMarkersConverter instance is null. init() must be called before getting the instance." }
            return INSTANCE!!
        }

        fun init(context: Context) {
            if (INSTANCE != null) {
                return
            }

            synchronized(this) {
                INSTANCE = EntriesToMapMarkersConverter()
                INSTANCE!!.loadResources(context)
                INSTANCE
            }
        }
    }

    private var tagLatitude: String? = null
    private var tagLongitude: String? = null
    private var entryTypeCiconia: String? = null
    private var tagSpecies1: String? = null
    private var tagSpecies2: String? = null
    private var tagThreatsPrimaryType: String? = null
    private var tagThreatsCategory: String? = null
    private var poisonedString: String? = null
    private var tagPylonType: String? = null


    private fun loadResources(context: Context) {
        val resources: Resources = context.resources

        this.tagLatitude = resources.getString(R.string.tag_lat)
        this.tagLongitude = resources.getString(R.string.tag_lon)
        this.entryTypeCiconia = resources.getString(R.string.entry_type_ciconia)
        this.tagSpecies1 = resources.getString(R.string.tag_species_scientific_name)
        this.tagSpecies2 = resources.getString(R.string.tag_observed_bird)
        this.tagThreatsPrimaryType = resources.getString(R.string.tag_primary_type)
        this.tagThreatsCategory = resources.getString(R.string.tag_category)
        this.poisonedString = resources.getString(R.string.monitoring_threats_poisoned)
        this.tagPylonType = resources.getString(R.string.tag_pylons_pylon_type)
    }

    override fun convert(item: MonitoringEntry): EntryMapMarker {
        val lat = item.data[tagLatitude]!!.toDouble()
        val lon = item.data[tagLongitude]!!.toDouble()
        var name: String? = null

        if (EntryType.CICONIA == item.type) {
            name = entryTypeCiconia
        } else if (EntryType.THREATS == item.type) {
            val primaryType = item.data[tagThreatsPrimaryType]
            name = if (FormsConfig.ThreatsPrimaryType.threat.isSame(primaryType)) {
                item.data[tagThreatsCategory]
            } else {
                poisonedString
            }
        } else if (EntryType.PYLONS == item.type) {
            name = item.data[tagPylonType]
        } else {
            if (item.data.containsKey(tagSpecies1)) {
                name = item.data[tagSpecies1]
            } else if (item.data.containsKey(tagSpecies2)) {
                name = item.data[tagSpecies2]
            }
        }
        return EntryMapMarker(name, lat, lon, item.id, item.type)
    }

}