package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.ui.map.MapProvider

class SettingsFragment : PreferenceFragmentCompat() {
    private var mapTypePreference: ListPreference? = null
    private var providerPreference: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        providerPreference = findPreference("providerType")
        mapTypePreference = findPreference("mapType")
        providerPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener() { _: Preference, newValue: Any ->
            updateMapType(MapProvider.ProviderType.valueOf(newValue as String))
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