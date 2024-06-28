package org.bspb.smartbirds.pro.prefs

import android.content.Context
import androidx.preference.PreferenceManager

class DownloadsPrefs(context: Context) {

    companion object {
        const val KEY_DOWNLOADS = "downloads"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)


    fun getDownloads(): String? {
        return prefs.getString(KEY_DOWNLOADS, "")
    }

    fun setDownloads(value: String?) {
        prefs.edit().putString(KEY_DOWNLOADS, value).apply()
    }

}