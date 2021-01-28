package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.ui.map.MapProvider
import org.bspb.smartbirds.pro.utils.debugLog
import org.bspb.smartbirds.pro.utils.showAlert

class SettingsFragment : PreferenceFragmentCompat() {
    private var mapTypePreference: ListPreference? = null
    private var providerPreference: ListPreference? = null
    private var enabledFormsPreference: MultiSelectListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        providerPreference = findPreference("providerType")
        mapTypePreference = findPreference("mapType")
        enabledFormsPreference = findPreference("formsEnabled")

        providerPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener() { _: Preference, newValue: Any ->
            updateMapType(MapProvider.ProviderType.valueOf(newValue as String))
            true
        }
        enabledFormsPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener() { _: Preference, newValue: Any ->
            if (newValue is Set<*> && newValue.isEmpty()) {
                context?.showAlert(R.string.settings_enabled_forms_alert_title, R.string.settings_enabled_forms_alert_message, null, null)
                return@OnPreferenceChangeListener false
            }
            true
        }



        updateMapType(MapProvider.ProviderType.valueOf(providerPreference?.value as String))
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