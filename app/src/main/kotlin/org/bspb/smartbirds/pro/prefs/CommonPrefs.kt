package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class CommonPrefs(context: Context) {

    companion object {
        const val KEY_COMMON_OTHER_OBSERVERS = "commonOtherObservers"
        const val KEY_CONFIDENTIAL_RECORD = "confidentialRecord"
    }

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getCommonOtherObservers(): String {
        return prefs.getString(KEY_COMMON_OTHER_OBSERVERS, "") ?: ""
    }

    fun setCommonOtherObservers(value: String?) {
        prefs.edit()?.putString(KEY_COMMON_OTHER_OBSERVERS, value)?.apply()
    }

    fun getConfidentialRecord(): Boolean {
        return prefs.getBoolean(KEY_CONFIDENTIAL_RECORD, false)
    }

    fun setConfidentialRecord(value: Boolean) {
        prefs.edit()?.putBoolean(KEY_CONFIDENTIAL_RECORD, value)?.apply()
    }
}