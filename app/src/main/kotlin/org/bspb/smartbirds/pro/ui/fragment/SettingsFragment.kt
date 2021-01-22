package org.bspb.smartbirds.pro.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.bspb.smartbirds.pro.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}