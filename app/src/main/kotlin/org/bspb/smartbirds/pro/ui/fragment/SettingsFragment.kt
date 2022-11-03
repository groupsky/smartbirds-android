package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.*
import com.google.gson.reflect.TypeToken
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.backend.dto.DownloadsItem
import org.bspb.smartbirds.pro.backend.dto.MapLayerItem
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.tools.DBExporter
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.ui.map.MapProvider
import org.bspb.smartbirds.pro.utils.KmlUtils
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.showAlert


class SettingsFragment : PreferenceFragmentCompat() {
    private var mapTypePreference: ListPreference? = null
    private var providerPreference: ListPreference? = null
    private var enabledFormsPreference: MultiSelectListPreference? = null
    private var exportPreference: Preference? = null
    private var showKmlPreference: SwitchPreferenceCompat? = null
    private var importedKmlPreference: Preference? = null
    private var prefs: SmartBirdsPrefs_? = null

    private val pickKml = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult

        prefs?.kmlFileName()?.put(KmlUtils.getFileName(requireContext(), it))
        KmlUtils.copyFileToExternalStorage(requireContext(), it)
        updateImportedKmlPreference()
        showKmlPreference?.isChecked = true

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        prefs = SmartBirdsPrefs_(requireContext())
        initPreferences()
        updateMapType(MapProvider.ProviderType.valueOf(providerPreference?.value as String))
    }

    private fun initPreferences() {
        providerPreference = findPreference("providerType")
        mapTypePreference = findPreference("mapType")
        enabledFormsPreference = findPreference("formsEnabled")
        exportPreference = findPreference("exportDB")

        providerPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener() { _: Preference, newValue: Any ->
                updateMapType(MapProvider.ProviderType.valueOf(newValue as String))
                true
            }
        enabledFormsPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener() { _: Preference, newValue: Any ->
                if (newValue is Set<*> && newValue.isEmpty()) {
                    context?.showAlert(
                        R.string.settings_enabled_forms_alert_title,
                        R.string.settings_enabled_forms_alert_message,
                        null,
                        null
                    )
                    return@OnPreferenceChangeListener false
                }
                true
            }
        exportPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener { _: Preference ->
                context?.let { DBExporter.exportDB(it) }
                return@OnPreferenceClickListener true
            }

        showKmlPreference = findPreference("showUserKml")
        showKmlPreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                if (!KmlUtils.checkExistingUserKml(requireContext())) {
                    pickKml()
                    return@setOnPreferenceChangeListener false
                }
            }

            return@setOnPreferenceChangeListener true
        }

        importedKmlPreference = findPreference("importKml")
        importedKmlPreference?.setOnPreferenceClickListener {
            pickKml()
            return@setOnPreferenceClickListener true
        }

        updateImportedKmlPreference()
        initMapLayersSettings()
    }

    private fun initMapLayersSettings() {
        val listType = object : TypeToken<List<MapLayerItem?>?>() {}.type
        val layersPreferenceCategory: PreferenceCategory? = findPreference("layersCategory")
        layersPreferenceCategory ?: return
        var enabledLayers = mutableSetOf<String>()
        enabledLayers.addAll(prefs?.enabledMapLayers()?.get() ?: emptySet())

        var mapLayers = SBGsonParser.createParser()
            .fromJson<List<MapLayerItem>>(prefs?.mapLayers()?.get(), listType)

        mapLayers ?: return

        mapLayers.forEach { mapLayerItem ->
            mapLayerItem.enabled ?: return@forEach
            val layerPreference = SwitchPreferenceCompat(requireContext())
            layerPreference.key = "map_layer_${mapLayerItem.id}"
            layerPreference.isIconSpaceReserved = false
            layerPreference.title = mapLayerItem.label?.get(getString(R.string.locale))
            layerPreference.summary = mapLayerItem.summary?.get(getString(R.string.locale))
            layerPreference.setOnPreferenceChangeListener { _, newValue ->

                if (newValue as Boolean) {
                    enabledLayers.add(mapLayerItem.id.toString())
                } else {
                    enabledLayers.remove(mapLayerItem.id.toString())
                }

                prefs?.enabledMapLayers()?.put(enabledLayers)

                return@setOnPreferenceChangeListener true
            }

            layersPreferenceCategory.addPreference(layerPreference)
        }

    }

    private fun updateImportedKmlPreference() {
        if (KmlUtils.checkExistingUserKml(requireContext())) {
            importedKmlPreference?.title = prefs?.kmlFileName()?.get() ?: "user.kml"
        }
    }

    private fun pickKml() {
        pickKml.launch("application/vnd.google-earth.kml+xml")
    }

    private fun updateMapType(provider: MapProvider.ProviderType) {
        if (provider == MapProvider.ProviderType.OSM) {
            mapTypePreference?.value = MapProvider.MapType.NORMAL.toString()
            mapTypePreference?.isEnabled = false
        } else {
            mapTypePreference?.isEnabled = true
        }
    }
}