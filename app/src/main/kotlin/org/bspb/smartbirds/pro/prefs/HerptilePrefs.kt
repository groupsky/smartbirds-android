package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences

class HerptilePrefs(context: Context) {

    private var prefs: SharedPreferences? = null

    companion object {
        const val KEY_HERPTILE_HABITAT = "herptileHabitat"
    }

    init {
        prefs =
            context.getSharedPreferences(
                (PrefsHelper.getLocalClassName(context) + "_HerptilePrefs"),
                0
            )
    }

    fun getHerptileHabitat(): String {
        return prefs?.getString(KEY_HERPTILE_HABITAT, "") ?: ""
    }

    fun setHerptileHabitat(value: String) {
        prefs?.edit()?.putString(KEY_HERPTILE_HABITAT, value)?.apply()
    }

}