package org.bspb.smartbirds.pro.prefs

import android.content.Context
import androidx.preference.PreferenceManager

class BatsPrefs(context: Context) {

    companion object {
        const val KEY_METODOLOGY = "metodology"
        const val KEY_TEMP_CAVE = "tempCave"
        const val KEY_HUMIDITY_CAVE = "humidityCave"
        const val KEY_TYPE_LOC = "typeLoc"
        const val KEY_HABITAT = "habitat"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun getMetodology(): String? {
        return prefs.getString(KEY_METODOLOGY, "")
    }

    fun setMetodology(value: String?) {
        prefs.edit().putString(KEY_METODOLOGY, value).apply()
    }

    fun getTempCave(): String? {
        return prefs.getString(KEY_TEMP_CAVE, "")
    }

    fun setTempCave(value: String?) {
        prefs.edit().putString(KEY_TEMP_CAVE, value).apply()
    }

    fun getHumidityCave(): String? {
        return prefs.getString(KEY_HUMIDITY_CAVE, "")
    }

    fun setHumidityCave(value: String?) {
        prefs.edit().putString(KEY_HUMIDITY_CAVE, value).apply()
    }

    fun getTypeLoc(): String? {
        return prefs.getString(KEY_TYPE_LOC, "")
    }

    fun setTypeLoc(value: String?) {
        prefs.edit().putString(KEY_TYPE_LOC, value).apply()
    }

    fun getHabitat(): String? {
        return prefs.getString(KEY_HABITAT, "")
    }

    fun setHabitat(value: String?) {
        prefs.edit().putString(KEY_HABITAT, value).apply()
    }


}
