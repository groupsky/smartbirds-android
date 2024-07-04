package org.bspb.smartbirds.pro.prefs

import android.content.Context
import android.content.SharedPreferences

class HerptilePrefs(context: Context) {

    companion object {
        const val KEY_HERPTILE_HABITAT = "herptileHabitat"
    }

    private var prefs: SharedPreferences = context.getSharedPreferences(
        (PrefsHelper.getLocalClassName(context) + "_HerptilePrefs"),
        0
    )

    fun getHerptileHabitat(): String {
        return prefs.getString(KEY_HERPTILE_HABITAT, "") ?: ""
    }

    fun setHerptileHabitat(value: String?) {
        prefs.edit()?.putString(KEY_HERPTILE_HABITAT, value)?.apply()
    }

}