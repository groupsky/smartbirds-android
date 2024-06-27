package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences

class BirdPrefs(context: Context) {

    private var prefs: SharedPreferences? = null

    companion object {
        const val KEY_BIRD_COUNT_UNITS = "birdCountUnits"
        const val KEY_BIRD_COUNT_TYPE = "birdCountType"
    }

    init {
        prefs =
            context.getSharedPreferences((PrefsHelper.getLocalClassName(context) + "_BirdPrefs"), 0)
    }

    fun getCountUnits(): String {
        return prefs?.getString(KEY_BIRD_COUNT_UNITS, "") ?: ""
    }

    fun setCountUnits(value: String) {
        prefs?.edit()?.putString(KEY_BIRD_COUNT_UNITS, value)?.apply()
    }

    fun getCountType(): String {
        return prefs?.getString(KEY_BIRD_COUNT_TYPE, "") ?: ""
    }

    fun setCountType(value: String) {
        prefs?.edit()?.putString(KEY_BIRD_COUNT_TYPE, value)?.apply()
    }

}