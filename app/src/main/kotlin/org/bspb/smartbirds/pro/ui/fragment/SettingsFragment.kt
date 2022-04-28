package org.bspb.smartbirds.pro.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.*
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.tools.DBExporter
import org.bspb.smartbirds.pro.ui.map.MapProvider
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.popToast
import org.bspb.smartbirds.pro.utils.showAlert
import java.io.File

class SettingsFragment : PreferenceFragmentCompat() {
    private var mapTypePreference: ListPreference? = null
    private var providerPreference: ListPreference? = null
    private var enabledFormsPreference: MultiSelectListPreference? = null
    private var exportPreference: Preference? = null

    private val pickKml = registerForActivityResult(ActivityResultContracts.GetContent()) {
        copyFileToExternalStorage(it)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
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
        exportPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener { _: Preference ->
            context?.let { DBExporter.exportDB(it) }
            return@OnPreferenceClickListener true
        }

        val showKmlPreference: SwitchPreferenceCompat? = findPreference("showUserKml")
        showKmlPreference?.setOnPreferenceChangeListener { preference, newValue ->
            debugLog("current: ${(preference as SwitchPreferenceCompat).isChecked}, newValue: $newValue")
            if (newValue as Boolean) {
                if (!checkExistingUserKml()) {
                    requireContext().popToast("Missing file. Should open picker")
                    pickKml()
                    return@setOnPreferenceChangeListener false
                }
            }

            return@setOnPreferenceChangeListener true
        }
    }

    private fun pickKml() {
        pickKml.launch("application/vnd.google-earth.kml+xml")
    }

    private fun checkExistingUserKml(): Boolean {
        return getKmlFile().exists()
    }

    private fun copyFileToExternalStorage(uri: Uri) {
        requireContext().contentResolver.openInputStream(uri)?.use {
            val kmlFile = getKmlFile()
            if (kmlFile.exists()) {
                kmlFile.delete()
            }
            it.copyTo(getKmlFile().outputStream())
        }
    }

    private fun getKmlFile(): File {
        val kmlFile = File(requireContext().getExternalFilesDir(null), "user.kml")
        debugLog("PAth: ${kmlFile.absolutePath}")
        return kmlFile
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